package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for syncing saved posts.
 *
 * @property repository The repository for post-related operations.
 */
class SyncSavedPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Synchronizes saved posts.
     *
     * @param page The page number for pagination.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the total number of posts or an exception if synchronization fails.
     */
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 20): Result<Int> =
        repository.syncSavedPosts(page, pageSize)
}