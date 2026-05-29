package com.flexaura.data.repository

import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.network.datasource.database.SupabaseRemoteDataSource
import com.flexaura.data.di.ApplicationScope
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import com.flexaura.data.mapper.toDomain
import com.flexaura.data.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val networkDataSource: SupabaseRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) : PostRepository {
    override fun getPostsFlow(): Flow<List<Post>> =
        postDao.getPostsFlow()
            .map { postEntities -> postEntities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override suspend fun getPostById(postId: String): Post? =
        postDao.getPostById(postId)?.toDomain()

    override suspend fun syncPosts(
        page: Int,
        pageSize: Int
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val remotePosts = networkDataSource.getPosts(page, pageSize)
            val postEntities = remotePosts.map { it.toEntity() }

            postDao.upsertPosts(postEntities)
        }
    }

    override suspend fun createPost(
        content: String?,
        imageUrl: String?
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            networkDataSource.createPost(content = content, imageUrl = imageUrl)
            syncPosts(page = 1, pageSize = 10).getOrThrow()
        }
    }.await()

    override suspend fun toggleSavePost(postId: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                val currentPost =
                    postDao.getPostById(postId) ?: throw Exception("No post found")
                val newSaveState = !currentPost.isSavedByUser

                postDao.updateLocalSaveState(postId, newSaveState)

                if (newSaveState) networkDataSource.savePost(postId)
                else networkDataSource.unsavePost(postId)
            }
        }.await()

    override suspend fun deletePost(postId: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                postDao.deletePostByid(postId)
                networkDataSource.deletePost(postId)
            }
        }.await()

    override suspend fun votePost(
        postId: String,
        voteValue: Int
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            postDao.updateLocalVote(postId = postId, voteValue = voteValue)

            if (voteValue == 0) networkDataSource.deleteVote(postId = postId)
            else networkDataSource.upsertVote(postId = postId, value = voteValue)
        }
    }.await()
}