package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for searching posts.
 *
 * @property repository The repository for post-related operations.
 */
class SearchPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Searches for posts with the specified query.
     *
     * @param query The search query.
     * @param page The page number for pagination.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing a list of [Post]s or an exception if the search fails.
     */
    suspend operator fun invoke(query: String, page: Int, pageSize: Int): Result<List<Post>> =
        repository.searchPosts(query, page, pageSize)
}