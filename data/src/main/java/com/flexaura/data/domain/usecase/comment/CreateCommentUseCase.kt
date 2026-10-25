package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import com.flexaura.data.domain.util.isValidLength
import javax.inject.Inject

/**
 * Use case for creating a new comment on a post.
 *
 * @property repository The repository for comment-related operations.
 */
class CreateCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    /**
     * Creates a new comment on the specified post.
     *
     * @param postId The ID of the post to comment on.
     * @param content The content of the comment.
     * @return A [Result] indicating success or containing an exception if creation fails.
     */
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