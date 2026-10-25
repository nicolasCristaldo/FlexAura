package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for retrieving a post by its ID.
 *
 * @property repository The repository for post-related operations.
 */
class GetPostByIdUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Retrieves the post with the specified ID.
     *
     * @param postId The ID of the post to retrieve.
     * @return The [Post] with the specified ID, or null if not found.
     */
    suspend operator fun invoke(postId: String): Post? = repository.getPostById(postId = postId)
}