package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import com.flexaura.data.domain.util.isValidLength
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId: String, content: String): Result<Unit> {
        val sanitizedContent = content
            .replace(Regex("\\n{2,}"), "\n")
            .trim()

        if (!sanitizedContent.isValidLength(1, 280)) {
            return Result.failure(IllegalArgumentException("The comment cannot exceed 280 characters"))
        }

        return repository.createComment(postId = postId, content = sanitizedContent)
    }
}