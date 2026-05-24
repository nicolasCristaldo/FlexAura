package com.flexaura.core.network.datasource

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.FollowDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.core.network.model.VoteDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
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

    override suspend fun updateUser(user: UserDto): UserDto =
        supabaseClient.postgrest["users"]
            .update(user) {
                filter { eq("id", user.id) }
            }.decodeSingle<UserDto>()

    override suspend fun getUserProfile(userId: String): UserProfileDto? {
        val currentId = requireCurrentUserId()
        return supabaseClient.postgrest["users"]
            .select(
                columns = Columns.raw(
                    """
                *,
                follows!following_id(id).filter(follower_id.eq.$currentId),
            """)
            ) {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<UserProfileDto>()
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
        val to = page * pageSize -1

        return supabaseClient.postgrest["follows"]
            .select(columns = Columns.raw("users!follower_id(*)")) {
                filter { eq("following_id", userId) }
                range(from.toLong(), to.toLong())
            }.decodeList<UserDto>()
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        val from = (page - 1) * pageSize
        val to = page * pageSize -1

        return supabaseClient.postgrest["follows"]
            .select(columns = Columns.raw("users!following_id(*)")) {
                filter { eq("follower_id", userId) }
                range(from.toLong(), to.toLong())
            }.decodeList<UserDto>()
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
        val to = page * pageSize -1

        return supabaseClient.postgrest["posts"]
            .select(columns = Columns.raw("""
                *,
                votes(value).filter(user_id.eq.$currentId),
                saves(id).filter(user_id.eq.$currentId)
            """)) {
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<PostWithInteractionsDto>()
    }

    override suspend fun createPost(post: PostDto): PostDto =
        supabaseClient.postgrest["posts"]
            .insert(post) {
                select()
            }.decodeSingle<PostDto>()

    override suspend fun deletePost(postId: String) {
        supabaseClient.postgrest["posts"]
            .delete {
                filter { eq("id", postId) }
            }
    }
    // -------------------- Votes --------------------
    override suspend fun upsertVote(vote: VoteDto) {
        supabaseClient.postgrest["votes"].upsert(vote)
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

    override suspend fun createComment(comment: CommentDto): CommentDto =
        supabaseClient.postgrest["comments"]
            .insert(comment) {
                select()
            }.decodeSingle<CommentDto>()

    override suspend fun deleteComment(commentId: String) {
        supabaseClient.postgrest["comments"]
            .delete {
                filter { eq("id", commentId) }
            }
    }

    // -------------------- Saves --------------------
    override suspend fun savePost(save: SaveDto) {
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
        val to = page * pageSize -1

        return supabaseClient.postgrest["saves"]
            .select(columns = Columns.raw("posts(*)")) {
                filter { eq("user_id", currentId) }
                order("created_at", Order.DESCENDING)
                range(from.toLong(), to.toLong())
            }.decodeList<PostDto>()
    }
}