package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import javax.inject.Inject

/**
 * Use case for deleting a comment.
 *
 * @property repository The repository for comment-related operations.
 */
class DeleteCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    /**
     * Deletes the specified comment.
     *
     * @param commentId The ID of the comment to delete.
     * @return A [Result] indicating success or containing an exception if deletion fails.
     */
    suspend operator fun invoke(commentId: String): Result<Unit> =
        repository.deleteComment(commentId = commentId)
}