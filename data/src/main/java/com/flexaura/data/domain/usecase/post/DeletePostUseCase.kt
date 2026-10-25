package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for deleting a post.
 *
 * @property repository The repository for post-related operations.
 */
class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Deletes the specified post.
     *
     * @param postId The ID of the post to delete.
     * @return A [Result] indicating success or containing an exception if deletion fails.
     */
    suspend operator fun invoke(postId: String): Result<Unit> =
        repository.deletePost(postId = postId)
}