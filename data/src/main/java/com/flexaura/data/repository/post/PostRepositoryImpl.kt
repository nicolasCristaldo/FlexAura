package com.flexaura.data.repository.post

import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.network.datasource.SupabaseRemoteDataSource
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.SaveDto
import com.flexaura.core.network.model.VoteDto
import com.flexaura.data.mapper.toDomain
import com.flexaura.data.mapper.toEntity
import com.flexaura.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val networkDataSource: SupabaseRemoteDataSource
) : PostRepository {
    override fun getPostsFlow(): Flow<List<Post>> =
        postDao.getPostsFlow()
            .map { postEntities ->
                postEntities.map { it.toDomain() }
            }

    override suspend fun getPostById(postId: String): Post? =
        postDao.getPostById(postId)?.toDomain()

    override suspend fun syncPosts(
        currentUserId: String,
        page: Int,
        pageSize: Int
    ): Result<Unit> = runCatching {
        val remotePosts = networkDataSource.getPosts(currentUserId, page, pageSize)
        val postEntities = remotePosts.map { it.toEntity() }

        postDao.upsertPosts(postEntities)
    }

    override suspend fun createPost(
        currentUserId: String,
        content: String,
        imageUrl: String
    ): Result<Unit> = runCatching {
        val newPostDto = PostDto(
            id = UUID.randomUUID().toString(),
            authorId = currentUserId,
            content = content,
            imageUrl = imageUrl,
            createdAt = ""
        )

        networkDataSource.createPost(newPostDto)

        syncPosts(
            currentUserId = currentUserId,
            page = 1,
            pageSize = 10
        ).getOrThrow()
    }

    override suspend fun toggleSavePost(
        currentUserId: String,
        postId: String
    ): Result<Unit> = runCatching {
        val currentPost = postDao.getPostById(postId) ?: return Result.failure(Exception("No post found"))
        val newSaveState = !currentPost.isSavedByUser

        postDao.updateLocalSaveState(postId, newSaveState)

        if(newSaveState) {
            networkDataSource.savePost(
                SaveDto(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserId,
                    postId = postId
                )
            )
        }
        else { networkDataSource.unsavePost(currentUserId, postId) }
    }

    override suspend fun deletePost(postId: String): Result<Unit> = runCatching {
        postDao.deletePostByid(postId)
        networkDataSource.deletePost(postId)
    }

    override suspend fun updateLocalVote(
        postId: String,
        voteValue: Int
    ): Result<Unit> = runCatching {
        postDao.updateLocalVote(postId, voteValue)
    }

    override suspend fun remoteUpsertVote(
        currentUserId: String,
        postId: String,
        voteValue: Int
    ): Result<Unit> = runCatching {
        networkDataSource.upsertVote(
            VoteDto(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                postId = postId,
                value = voteValue
            )
        )
    }

    override suspend fun remoteDeleteVote(
        currentUserId: String,
        postId: String
    ): Result<Unit> = runCatching {
        networkDataSource.deleteVote(currentUserId, postId)
    }
}