package com.flexaura.data.repository

import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.network.datasource.database.SupabaseRemoteDataSource
import com.flexaura.data.di.ApplicationScope
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.MediaRepository
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
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val networkDataSource: SupabaseRemoteDataSource,
    private val mediaRepository: MediaRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) : PostRepository {
    override fun getPostsFlow(): Flow<List<Post>> =
        postDao.getPostsFlow()
            .map { postEntities -> postEntities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override suspend fun getPostById(postId: String): Post? =
        postDao.getPostById(postId)?.toDomain()

    override fun getPostsByAuthorFlow(userId: String): Flow<List<Post>> =
        postDao.getPostsByAuthorFlow(userId)
            .map { postEntities -> postEntities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override fun getSavedPostsFlow(): Flow<List<Post>> =
        postDao.getSavedPostsFlow()
            .map { postEntities -> postEntities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override fun getFollowingPosts(): Flow<List<Post>> =
        postDao.getFollowingPostsFlow()
            .map { postEntities -> postEntities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override suspend fun syncPosts(
        page: Int,
        pageSize: Int
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val remotePosts = networkDataSource.getPosts(page, pageSize)

            val usersToUpsert = remotePosts.mapNotNull { it.author?.toEntity() }.distinct()
            if(usersToUpsert.isNotEmpty()) {
                userDao.upsertUsers(usersToUpsert)
            }

            val postEntities = remotePosts.map { it.toEntity() }
            postDao.upsertPosts(postEntities)
        }
    }

    override suspend fun syncPostsByAuthor(
        userId: String,
        page: Int,
        pageSize: Int
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val remotePosts = networkDataSource.getPostsByAuthor(userId, page, pageSize)

            val usersToUpsert = remotePosts.mapNotNull { it.author?.toEntity(isCurrentUserFollowing = false) }.distinct()
            if(usersToUpsert.isNotEmpty()) {
                userDao.upsertUsers(usersToUpsert)
            }

            val postEntities = remotePosts.map { it.toEntity() }
            postDao.upsertPosts(postEntities)
        }
    }

    override suspend fun syncSavedPosts(
        page: Int,
        pageSize: Int
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val remotePosts = networkDataSource.getSavedPosts(page, pageSize)

            val usersToUpsert = remotePosts.mapNotNull {
                it.author?.toEntity(isCurrentUserFollowing = false)
            }.distinct()

            if(usersToUpsert.isNotEmpty()) {
                userDao.upsertUsers(usersToUpsert)
            }

            val postEntities = remotePosts.map { it.toEntity() }
            postDao.upsertPosts(postEntities)
        }
    }

    override suspend fun syncFollowingPosts(page: Int, pageSize: Int): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                val remotePosts = networkDataSource.getFollowingPosts(page, pageSize)
                val usersToUpsert = remotePosts.mapNotNull {
                    it.author?.toEntity(isCurrentUserFollowing = true)
                }.distinct()

                if(usersToUpsert.isNotEmpty()) {
                    userDao.upsertUsers(usersToUpsert)
                }

                val postsEntities = remotePosts.map { it.toEntity() }
                postDao.upsertPosts(postsEntities)
            }
        }

    override suspend fun createPost(
        content: String?,
        imageBytes: ByteArray?
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            val postId = UUID.randomUUID().toString()
            var uploadedImageUrl: String? = null

            if(imageBytes != null) {
                uploadedImageUrl = mediaRepository.uploadPostImage(
                    postId = postId,
                    imageBytes = imageBytes
                ).getOrThrow()
            }

            networkDataSource.createPost(
                id = postId,
                content = content,
                imageUrl = uploadedImageUrl
            )
            syncPosts(page = 1, pageSize = 10).getOrThrow()
        }
    }.await()

    override suspend fun toggleSavePost(postId: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                val currentPost =
                    postDao.getPostById(postId) ?: throw Exception("No post found")
                val newSaveState = !currentPost.post.isSavedByUser
                val newSavesCount = if (newSaveState) currentPost.post.savesCount + 1
                                    else (currentPost.post.savesCount - 1).coerceAtLeast(0)

                postDao.updateLocalSaveState(postId, newSaveState, newSavesCount)

                if (newSaveState) networkDataSource.savePost(postId)
                else networkDataSource.unsavePost(postId)
            }
        }.await()

    override suspend fun deletePost(postId: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                val localPost = postDao.getPostById(postId)

                localPost?.post?.imageUrl.takeIf { it?.isNotBlank() ?: false }?.let { imageUrl ->
                    val fileName = imageUrl.substringAfterLast("/")
                    if(fileName.isNotEmpty()) {
                        runCatching {
                            mediaRepository.deleteImage(bucketName = "posts", fileName = fileName)
                        }
                    }
                }

                postDao.deletePostById(postId)
                networkDataSource.deletePost(postId)
            }
        }.await()

    override suspend fun votePost(
        postId: String,
        voteValue: Int
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            val currentPost = postDao.getPostById(postId) ?: throw Exception("No post found")
            val oldVote = currentPost.post.currentUserVote
            val diff = voteValue - oldVote
            val newTotalAura = currentPost.post.totalAura + diff

            postDao.updateLocalVote(postId = postId, voteValue = voteValue, newTotalAura = newTotalAura)

            if (voteValue == 0) networkDataSource.deleteVote(postId = postId)
            else networkDataSource.upsertVote(postId = postId, value = voteValue)
        }
    }.await()

    override suspend fun searchPosts(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<Post>> = withContext(dispatcher) {
        runCatching {
            val remotePosts = networkDataSource.searchPosts(query, page, pageSize)

            val usersToUpsert = remotePosts.mapNotNull { it.author?.toEntity(isCurrentUserFollowing = false) }.distinct()
            if(usersToUpsert.isNotEmpty()) {
                userDao.upsertUsers(usersToUpsert)
            }

            val postEntities = remotePosts.map { it.toEntity() }
            postDao.upsertPosts(postEntities)
            postEntities.map { it.toDomain() }
        }
    }
}