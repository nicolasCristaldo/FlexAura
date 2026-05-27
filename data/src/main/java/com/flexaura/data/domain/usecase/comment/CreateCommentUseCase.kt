package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId: String, content: String): Result<Unit> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("The comment cannot be empty"))
        }

        return repository.createComment(postId = postId, content = content.trim())
    }
}