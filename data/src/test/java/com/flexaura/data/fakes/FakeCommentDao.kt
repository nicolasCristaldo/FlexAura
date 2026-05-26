package com.flexaura.data.fakes

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

    override suspend fun insertComment(comment: CommentEntity) {
        val currentComments = commentsTable.value.toMutableList()

        currentComments.removeAll { it.id == comment.id }
        currentComments.add(comment)
        commentsTable.value = currentComments
    }

    override suspend fun deleteCommentById(commentId: String) {
        commentsTable.value = commentsTable.value.filter { it.id != commentId }
    }

    override suspend fun deleteCommentsByPostId(postId: String) {
        commentsTable.value = commentsTable.value.filter { it.postId != postId }
    }
}