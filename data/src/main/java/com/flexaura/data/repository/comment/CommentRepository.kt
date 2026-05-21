package com.flexaura.data.repository.comment

import com.flexaura.data.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getCommentsFlow(postId: String): Flow<List<Comment>>
    suspend fun syncComments(postId: String): Result<Unit>
    suspend fun createComment(currentUserId: String, postId: String, content: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
}