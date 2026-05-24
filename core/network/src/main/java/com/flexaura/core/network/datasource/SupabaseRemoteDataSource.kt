package com.flexaura.core.network.datasource

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.core.network.model.VoteDto

interface SupabaseRemoteDataSource  {
    val currentUserId: String?
    // Users
    suspend fun getUser(userId: String): UserDto?
    suspend fun updateUser(user: UserDto): UserDto
    suspend fun getUserProfile(userId: String): UserProfileDto?

    // Followers
    suspend fun followUser(followingId: String)
    suspend fun unfollowUser(followingId: String)
    suspend fun getFollowers(userId: String, page: Int, pageSize: Int): List<UserDto>
    suspend fun getFollowing(userId: String, page: Int, pageSize: Int): List<UserDto>

    // Posts
    suspend fun getPost(postId: String): PostDto?
    suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>
    suspend fun createPost(post: PostDto): PostDto
    suspend fun deletePost(postId: String)

    // Votes
    suspend fun upsertVote(vote: VoteDto)
    suspend fun deleteVote(postId: String)

    // Comments
    suspend fun getComments(postId: String): List<CommentDto>
    suspend fun createComment(comment: CommentDto): CommentDto
    suspend fun deleteComment(commentId: String)

    // Saves
    suspend fun savePost(save: SaveDto)
    suspend fun unsavePost(postId: String)
    suspend fun getSavedPosts(page: Int, pageSize: Int): List<PostDto>
}