package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(commentId: String): Result<Unit> =
        repository.deleteComment(commentId = commentId)
}