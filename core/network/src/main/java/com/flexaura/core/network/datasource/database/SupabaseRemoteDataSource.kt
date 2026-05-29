package com.flexaura.core.network.datasource.database

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto

interface SupabaseRemoteDataSource  {
    val currentUserId: String?
    // Users
    suspend fun getUser(userId: String): UserDto?
    suspend fun updateProfile(username: String, displayName: String, imageUrl: String?, bio: String?): UserDto
    suspend fun getUserProfile(userId: String): UserProfileDto?
    suspend fun insertProfile(userId: String, username: String, displayName: String): UserDto

    // Followers
    suspend fun followUser(followingId: String)
    suspend fun unfollowUser(followingId: String)
    suspend fun getFollowers(userId: String, page: Int, pageSize: Int): List<UserDto>
    suspend fun getFollowing(userId: String, page: Int, pageSize: Int): List<UserDto>

    // Posts
    suspend fun getPost(postId: String): PostDto?
    suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>
    suspend fun createPost(content: String?, imageUrl: String?): PostDto
    suspend fun deletePost(postId: String)

    // Votes
    suspend fun upsertVote(postId: String, value: Int)
    suspend fun deleteVote(postId: String)

    // Comments
    suspend fun getComments(postId: String): List<CommentDto>
    suspend fun createComment(postId: String, content: String): CommentDto
    suspend fun deleteComment(commentId: String)

    // Saves
    suspend fun savePost(postId: String)
    suspend fun unsavePost(postId: String)
    suspend fun getSavedPosts(page: Int, pageSize: Int): List<PostDto>
}