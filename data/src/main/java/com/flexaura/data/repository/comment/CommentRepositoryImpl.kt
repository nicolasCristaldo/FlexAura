package com.flexaura.data.repository.comment

import com.flexaura.core.database.dao.CommentDao
import com.flexaura.core.network.datasource.SupabaseRemoteDataSource
import com.flexaura.core.network.model.CommentDto
import com.flexaura.data.mapper.toDomain
import com.flexaura.data.mapper.toEntity
import com.flexaura.data.model.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao,
    private val networkDataSource: SupabaseRemoteDataSource
): CommentRepository {
    override fun getCommentsFlow(postId: String): Flow<List<Comment>> =
        commentDao.getCommentsByPostId(postId).map { commentEntities ->
            commentEntities.map { it.toDomain() }
        }

    override suspend fun syncComments(postId: String): Result<Unit> = runCatching {
        val remoteComments = networkDataSource.getComments(postId)
        val uniqueAuthorsIds = remoteComments.map { it.authorId }.distinct()
        val authorsMap = uniqueAuthorsIds.mapNotNull { authorId ->
            networkDataSource.getUser(authorId)?.let { userDto ->
                authorId to userDto
            }
        }.toMap()
        val commentEntities = remoteComments.mapNotNull { dto ->
            val authorDto = authorsMap[dto.authorId]
            authorDto?.let { dto.toEntity(it) }
        }

        commentEntities.forEach { commentDao.insertComment(it) }
    }

    override suspend fun createComment(
        currentUserId: String,
        postId: String,
        content: String
    ): Result<Unit> = runCatching {
        val newCommentDto = CommentDto(
            id = UUID.randomUUID().toString(),
            postId = postId,
            authorId = currentUserId,
            content = content,
            createdAt = ""
        )

        networkDataSource.createComment(newCommentDto)
        syncComments(postId).getOrThrow()
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> = runCatching {
        commentDao.deleteCommentById(commentId)
        networkDataSource.deleteComment(commentId)
    }
}