package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.model.Post
import com.flexaura.data.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the posts of users the current user follows.
 *
 * @property repository The repository for post-related operations.
 */
class GetFollowingPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Retrieves the posts of users the current user follows.
     *
     * @return A [Flow] emitting a list of [Post]s.
     */
    operator fun invoke(): Flow<List<Post>> = repository.getFollowingPosts()
}