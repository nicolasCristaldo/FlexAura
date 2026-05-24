package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getCommentsFlow(postId: String): Flow<List<Comment>>
    suspend fun syncComments(postId: String): Result<Unit>
    suspend fun createComment(currentUserId: String, postId: String, content: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
}