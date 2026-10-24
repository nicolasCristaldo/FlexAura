package com.flexaura.core.network.datasource.database.post

import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto

interface PostRemoteDataSource {
    // Posts
    suspend fun getPost(postId: String): PostDto?
    suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>
    suspend fun createPost(id: String, content: String?, imageUrl: String?): PostDto
    suspend fun deletePost(postId: String)
    suspend fun getPostsByAuthor(authorId: String, page: Int, pageSize: Int): List<PostWithInteractionsDto>
    suspend fun searchPosts(query: String, page: Int, pageSize: Int): List<PostWithInteractionsDto>
    suspend fun getFollowingPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>

    // Votes
    suspend fun upsertVote(postId: String, value: Int)
    suspend fun deleteVote(postId: String)

    // Saves
    suspend fun savePost(postId: String)
    suspend fun unsavePost(postId: String)
    suspend fun getSavedPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>
}