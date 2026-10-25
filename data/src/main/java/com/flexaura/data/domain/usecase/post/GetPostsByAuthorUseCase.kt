package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving posts by an author.
 *
 * @property repository The repository for post-related operations.
 */
class GetPostsByAuthorUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Retrieves posts by the specified author.
     *
     * @param userId The ID of the author.
     * @return A [Flow] emitting a list of [Post]s.
     */
    operator fun invoke(userId: String): Flow<List<Post>> = repository.getPostsByAuthorFlow(userId)
}