package com.flexaura.data.fakes.dao

import com.flexaura.core.database.dao.CommentDao
import com.flexaura.core.database.model.CommentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCommentDao : CommentDao {
    private val commentsTable = MutableStateFlow<List<CommentEntity>>(emptyList())

    override fun getCommentsByPostId(postId: String): Flow<List<CommentEntity>> =
        commentsTable.map { comments ->
            comments.filter { it.postId == postId }
        }

    override suspend fun getCommentById(commentId: String): CommentEntity? {
        commentsTable.value.forEach { comment ->
            if (comment.id == commentId) {
                return comment
            }
        }
        return null
    }

    override suspend fun insertComment(comment: CommentEntity) {
        val currentComments = commentsTable.value.toMutableList()

        currentComments.removeAll { it.id == comment.id }
        currentComments.add(comment)
        commentsTable.value = currentComments
    }

    override suspend fun insertComments(comments: List<CommentEntity>) {
        val currentComments = commentsTable.value.toMutableList()

        currentComments.addAll(comments)
        commentsTable.value = currentComments
    }

    override suspend fun deleteCommentById(commentId: String) {
        commentsTable.value = commentsTable.value.filter { it.id != commentId }
    }

    override suspend fun deleteCommentsByPostId(postId: String) {
        commentsTable.value = commentsTable.value.filter { it.postId != postId }
    }
}