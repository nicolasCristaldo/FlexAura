package com.flexaura.core.network.datasource.database.post

import com.flexaura.core.network.datasource.auth.UserSession
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.SavedPostsResponseDto
import com.flexaura.core.network.model.VoteDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [PostRemoteDataSource] utilizing the Supabase client and user session for remote post management.
 *
 * @property supabaseClient The Supabase client instance used for database queries and mutations.
 * @property userSession Helper class to obtain the currently authenticated user's ID.
 */
@Singleton
class PostRemoteDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val userSession: UserSession
): PostRemoteDataSource {
    // -------------------- Posts --------------------
    override suspend fun getPost(postId: String): PostDto? =
        supabaseClient.postgrest["posts"]
            .select {
                filter { eq("id", postId) }
            }.decodeSingleOrNull<PostDto>()

    override suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto> {
        val currentId = userSession.requireCurrentUserId()
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["posts"]
            .select(
                columns = Columns.Companion.raw("""
                *,
                users(*, follows!following_id(id)),
                votes!left(*),
                saves!left(*)
              """.trimIndent()))
            {
                filter {
                    eq("votes.user_id", currentId)
                    eq("saves.user_id", currentId)
                    eq("users.follows.follower_id", currentId)
                }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<PostWithInteractionsDto>()
    }

    override suspend fun createPost(id: String, content: String?, imageUrl: String?): PostDto {
        val currentId = userSession.requireCurrentUserId()
        val postBody = buildJsonObject {
            put("id", id)
            put("author_id", currentId)
            put("content", content)
            put("image_url", imageUrl)
        }

        return supabaseClient.postgrest["posts"]
            .insert(postBody) {
                select()
            }.decodeSingle<PostDto>()
    }

    override suspend fun deletePost(postId: String) {
        supabaseClient.postgrest["posts"]
            .delete {
                filter { eq("id", postId) }
            }
    }

    override suspend fun getPostsByAuthor(
        authorId: String,
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val currentId = userSession.requireCurrentUserId()
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["posts"]
            .select(columns = Columns.Companion.raw("""
                *,
                users(*),
                votes!left(*),
                saves!left(*)
              """.trimIndent())) {
                filter {
                    eq("author_id", authorId)
                    eq("votes.user_id", currentId)
                    eq("saves.user_id", currentId)
                }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<PostWithInteractionsDto>()
    }

    override suspend fun searchPosts(
        query: String,
        page: Int,
        pageSize: Int
    ): List<PostWithInteractionsDto> {
        val currentId = userSession.requireCurrentUserId()
        val searchPattern = "%$query%"
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["posts"]
            .select(
                columns = Columns.Companion.raw("""
                *,
                users(*),
                votes!left(*),
                saves!left(*)
              """.trimIndent()))
            {
                filter {
                    ilike("content", searchPattern)
                    eq("votes.user_id", currentId)
                    eq("saves.user_id", currentId)
                }
                range(from = from.toLong(), to = to.toLong())
            }.decodeList<PostWithInteractionsDto>()
    }

    override suspend fun getFollowingPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto> {
        val currentId = userSession.requireCurrentUserId()
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["posts"]
            .select(
                columns = Columns.Companion.raw("""
                *,
                users!inner(*, follows!following_id!inner(*)),
                votes!left(*),
                saves!left(*)
              """.trimIndent()
                )
            ) {
                filter {
                    eq("users.follows.follower_id", currentId)
                    eq("votes.user_id", currentId)
                    eq("saves.user_id", currentId)
                }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<PostWithInteractionsDto>()
    }

    // -------------------- Votes --------------------
    override suspend fun upsertVote(postId: String, value: Int) {
        val currentId = userSession.requireCurrentUserId()
        val vote = VoteDto(
            id = UUID.randomUUID().toString(),
            userId = currentId,
            postId = postId,
            value = value
        )

        supabaseClient.postgrest["votes"].upsert(vote) {
            onConflict = "user_id,post_id"
        }
    }

    override suspend fun deleteVote(postId: String) {
        val currentId = userSession.requireCurrentUserId()
        supabaseClient.postgrest["votes"]
            .delete {
                filter {
                    eq("post_id", postId)
                    eq("user_id", currentId)
                }
            }
    }

    // -------------------- Saves --------------------
    override suspend fun savePost(postId: String) {
        val currentId = userSession.requireCurrentUserId()
        val save = SaveDto(
            id = UUID.randomUUID().toString(),
            userId = currentId,
            postId = postId
        )

        supabaseClient.postgrest["saves"].insert(save)
    }

    override suspend fun unsavePost(postId: String) {
        val currentId = userSession.requireCurrentUserId()
        supabaseClient.postgrest["saves"]
            .delete {
                filter {
                    eq("user_id", currentId)
                    eq("post_id", postId)
                }
            }
    }

    override suspend fun getSavedPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto> {
        val currentId = userSession.requireCurrentUserId()
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["saves"]
            .select(
                columns = Columns.Companion.raw("""
                   posts(*, users(*), votes!left(*), saves!left(*))
                """.trimIndent())
            ) {
                filter {
                    eq("user_id", currentId)
                    eq("posts.votes.user_id", currentId)
                    eq("posts.saves.user_id", currentId)
                }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<SavedPostsResponseDto>().map { it.post }
    }
}