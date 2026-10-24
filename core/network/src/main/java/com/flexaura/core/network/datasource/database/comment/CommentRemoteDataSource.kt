package com.flexaura.core.network.datasource.database.comment

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.CommentWithAuthorDto

interface CommentRemoteDataSource {
    suspend fun getComments(postId: String): List<CommentWithAuthorDto>
    suspend fun createComment(postId: String, content: String): CommentDto
    suspend fun deleteComment(commentId: String)
}