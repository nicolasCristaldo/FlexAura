package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.model.Comment
import com.flexaura.data.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving comments for a specific post.
 *
 * @property repository The repository for comment-related operations.
 */
class GetCommentsUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    /**
     * Retrieves comments for the specified post.
     *
     * @param postId The ID of the post to retrieve comments for.
     * @return A [Flow] emitting a list of [Comment]s.
     */
    operator fun invoke(postId: String): Flow<List<Comment>> =
        repository.getCommentsFlow(postId = postId)
}