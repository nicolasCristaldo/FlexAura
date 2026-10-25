package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for toggling the save status of a post.
 *
 * @property repository The repository for post-related operations.
 */
class ToggleSavePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Toggles the save status of a post.
     *
     * @param postId The ID of the post to toggle the save status for.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(postId: String): Result<Unit> =
        repository.toggleSavePost(postId = postId)
}