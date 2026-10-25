package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for syncing posts.
 *
 * @property repository The repository for post-related operations.
 */
class SyncPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Synchronizes posts.
     *
     * @param page The page number for pagination.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the total number of posts or an exception if synchronization fails.
     */
    suspend operator fun invoke(page: Int, pageSize: Int = 20): Result<Int> =
        repository.syncPosts(page = page, pageSize = pageSize)
}