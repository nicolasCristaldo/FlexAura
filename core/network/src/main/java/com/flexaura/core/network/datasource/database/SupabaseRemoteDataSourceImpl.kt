package com.flexaura.core.network.datasource.database

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.FollowDto
import com.flexaura.core.network.model.FollowResponseDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.SavedPostsResponseDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.core.network.model.VoteDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRemoteDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : SupabaseRemoteDataSource {
    override val currentUserId: String?
        get() = supabaseClient.auth.currentUserOrNull()?.id

    private fun requireCurrentUserId(): String =
        currentUserId ?: throw Exception("No user logged in")

    // -------------------- Users --------------------
    override suspend fun getUser(userId: String): UserDto? =
        supabaseClient.postgrest["users"]
            .select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<UserDto>()

    override suspend fun updateProfile(
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?
    ): UserDto {
        val currentId = requireCurrentUserId()
        val updateBody = buildJsonObject {
            put("username", username)
            put("display_name", displayName)
            put("image_url", imageUrl)
            put("bio", bio)
        }

        return supabaseClient.postgrest["users"]
            .update(updateBody) {
                filter { eq("id", currentId) }
            }.decodeSingle<UserDto>()
    }

    override suspend fun getUserProfile(userId: String): UserProfileDto? {
        val currentId = requireCurrentUserId()

        return supabaseClient.postgrest["users"]
            .select(
                columns = Columns.raw(
                    """
                *,
                follows!following_id(id).filter(follower_id.eq.$currentId),
                """.trimIndent()
                )
            ) {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<UserProfileDto>()
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val searchPattern = "%$query%"
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["users"]
            .select {
                filter {
                    or {
                        ilike("username", searchPattern)
                        ilike("display_name", searchPattern)
                        ilike("bio", searchPattern)
                    }
                }
                range(from.toLong(), to.toLong())
            }.decodeList<UserDto>()
    }

    // -------------------- Followers --------------------
    override suspend fun followUser(followingId: String) {
        val currentId = requireCurrentUserId()
        val follow = FollowDto(followerId = currentId, followingId = followingId)
        supabaseClient.postgrest["follows"].insert(follow)
    }

    override suspend fun unfollowUser(followingId: String) {
        val currentId = requireCurrentUserId()
        supabaseClient.postgrest["follows"]
            .delete {
                filter {
                    eq("follower_id", currentId)
                    eq("following_id", followingId)
                }
            }
    }

    override suspend fun getFollowers(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["follows"]
            .select(columns = Columns.Companion.raw("users!follower_id(*)")) {
                filter { eq("following_id", userId) }
                range(from.toLong(), to.toLong())
            }.decodeList<FollowResponseDto>().map { it.user }
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["follows"]
            .select(columns = Columns.Companion.raw("users!following_id(*)")) {
                filter { eq("follower_id", userId) }
                range(from.toLong(), to.toLong())
            }.decodeList<FollowResponseDto>().map { it.user }
    }

    // -------------------- Posts --------------------
    override suspend fun getPost(postId: String): PostDto? =
        supabaseClient.postgrest["posts"]
            .select {
                filter { eq("id", postId) }
            }.decodeSingleOrNull<PostDto>()

    override suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto> {
        val currentId = requireCurrentUserId()
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

    override suspend fun createPost(content: String?, imageUrl: String?): PostDto {
        val currentId = requireCurrentUserId()
        val postBody = buildJsonObject {
            put("id", UUID.randomUUID().toString())
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
        val currentId = requireCurrentUserId()
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
        val currentId = requireCurrentUserId()
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
        val currentId = requireCurrentUserId()
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
        val currentId = requireCurrentUserId()
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
        val currentId = requireCurrentUserId()
        supabaseClient.postgrest["votes"]
            .delete {
                filter {
                    eq("post_id", postId)
                    eq("user_id", currentId)
                }
            }
    }

    // -------------------- Comments --------------------
    override suspend fun getComments(postId: String): List<CommentDto> =
        supabaseClient.postgrest["comments"]
            .select {
                filter { eq("post_id", postId) }
                order("created_at", Order.DESCENDING)
            }.decodeList<CommentDto>()

    override suspend fun createComment(postId: String, content: String): CommentDto {
        val currentId = requireCurrentUserId()
        val comment = CommentDto(
            id = UUID.randomUUID().toString(),
            postId = postId,
            authorId = currentId,
            content = content,
            createdAt = ""
        )

        return supabaseClient.postgrest["comments"]
            .insert(comment) {
                select()
            }.decodeSingle<CommentDto>()
    }

    override suspend fun deleteComment(commentId: String) {
        supabaseClient.postgrest["comments"]
            .delete {
                filter { eq("id", commentId) }
            }
    }

    // -------------------- Saves --------------------
    override suspend fun savePost(postId: String) {
        val currentId = requireCurrentUserId()
        val save = SaveDto(
            id = UUID.randomUUID().toString(),
            userId = currentId,
            postId = postId
        )

        supabaseClient.postgrest["saves"].insert(save)
    }

    override suspend fun unsavePost(postId: String) {
        val currentId = requireCurrentUserId()
        supabaseClient.postgrest["saves"]
            .delete {
                filter {
                    eq("user_id", currentId)
                    eq("post_id", postId)
                }
            }
    }

    override suspend fun getSavedPosts(page: Int, pageSize: Int): List<PostDto> {
        val currentId = requireCurrentUserId()
        val from = (page - 1) * pageSize
        val to = page * pageSize - 1

        return supabaseClient.postgrest["saves"]
            .select(columns = Columns.Companion.raw("posts(*)")) {
                filter { eq("user_id", currentId) }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<SavedPostsResponseDto>().map { it.post }
    }
}