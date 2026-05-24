package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPostsFlow(): Flow<List<Post>>
    suspend fun getPostById(postId: String): Post?
    suspend fun syncPosts(currentUserId: String, page: Int, pageSize: Int): Result<Unit>
    suspend fun createPost(currentUserId: String, content: String, imageUrl: String): Result<Unit>
    suspend fun toggleSavePost(currentUserId: String, postId: String): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun updateLocalVote(postId: String, voteValue: Int): Result<Unit>
    suspend fun remoteUpsertVote(currentUserId: String, postId: String, voteValue: Int): Result<Unit>
    suspend fun remoteDeleteVote(currentUserId: String, postId: String): Result<Unit>
}