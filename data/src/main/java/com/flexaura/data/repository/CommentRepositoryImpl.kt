package com.flexaura.data.repository

import com.flexaura.core.database.dao.CommentDao
import com.flexaura.core.database.dao.PostDao
import com.flexaura.core.network.datasource.database.comment.CommentRemoteDataSource
import com.flexaura.data.di.ApplicationScope
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.model.Comment
import com.flexaura.data.domain.repository.CommentRepository
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

/**
 * Implementation of [CommentRepository] utilizing local Room DAOs and remote data sources
 * to manage comment workflows and cache consistency.
 *
 * @property commentDao Data Access Object for local comment caching and queries.
 * @property postDao Data Access Object for updating local comment counters.
 * @property dataSource Remote data source for comment operations.
 * @property dispatcher The coroutine dispatcher for background thread execution.
 * @property scope The coroutine scope used for concurrent asynchronous tasks.
 */
class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao,
    private val postDao: PostDao,
    private val dataSource: CommentRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
): CommentRepository {
    override fun getCommentsFlow(postId: String): Flow<List<Comment>> =
        commentDao.getCommentsByPostId(postId).map { commentEntities ->
            commentEntities.map { it.toDomain() }
        }.flowOn(dispatcher)

    override suspend fun syncComments(postId: String): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val remoteComments = dataSource.getComments(postId)
            val commentEntities = remoteComments.map { it.toEntity() }

            commentDao.insertComments(commentEntities)
        }
    }

    override suspend fun createComment(
        postId: String,
        content: String
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            dataSource.createComment(postId = postId, content = content)
            postDao.updateLocalCommentsCount(postId = postId, increment = 1)

            try {
                syncComments(postId).getOrThrow()
            }
            catch (e: Exception) {
                postDao.updateLocalCommentsCount(postId = postId, increment = -1)
                throw e
            }
        }
    }.await()

    override suspend fun deleteComment(commentId: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                val comment = commentDao.getCommentById(commentId = commentId)

                commentDao.deleteCommentById(commentId)

                comment?.let {
                    postDao.updateLocalCommentsCount(postId = it.postId, increment = -1)
                }

                dataSource.deleteComment(commentId)
            }
        }.await()
}