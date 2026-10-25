package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import javax.inject.Inject

/**
 * Use case for syncing comments for a specific post.
 *
 * @property repository The repository for comment-related operations.
 */
class SyncCommentsUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    /**
     * Synchronizes comments for the specified post.
     *
     * @param postId The ID of the post to synchronize comments for.
     * @return A [Result] indicating success or containing an exception if synchronization fails.
     */
    suspend operator fun invoke(postId: String): Result<Unit> =
        repository.syncComments(postId = postId)
}