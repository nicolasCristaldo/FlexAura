package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPostsFlow(): Flow<List<Post>>
    suspend fun getPostById(postId: String): Post?
    fun getPostsByAuthorFlow(userId: String): Flow<List<Post>>
    fun getFollowingPosts(): Flow<List<Post>>
    suspend fun syncPosts(page: Int, pageSize: Int): Result<Unit>
    suspend fun syncPostsByAuthor(userId: String, page: Int, pageSize: Int): Result<Unit>
    suspend fun syncFollowingPosts(page: Int, pageSize: Int): Result<Unit>
    suspend fun createPost(content: String?, imageUrl: String?): Result<Unit>
    suspend fun toggleSavePost(postId: String): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>
    suspend fun votePost(postId: String, voteValue: Int): Result<Unit>
    suspend fun searchPosts(query: String, page: Int, pageSize: Int): Result<List<Post>>
}