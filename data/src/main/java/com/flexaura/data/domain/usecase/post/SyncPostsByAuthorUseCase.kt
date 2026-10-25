package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for syncing posts by an author.
 *
 * @property repository The repository for post-related operations.
 */
class SyncPostsByAuthorUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Synchronizes posts by the specified author.
     *
     * @param userId The ID of the author.
     * @param page The page number for pagination.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the total number of posts or an exception if synchronization fails.
     */
    suspend operator fun invoke(userId: String, page: Int = 1, pageSize: Int = 20): Result<Int> =
        repository.syncPostsByAuthor(userId, page, pageSize)
}