package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

/**
 * Use case for voting on a post.
 *
 * @property postRepository The repository for post-related operations.
 */
class VotePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    /**
     * Votes on a post.
     *
     * @param postId The ID of the post to vote on.
     * @param value The value of the vote (1 for upvote, -1 for downvote, 0 for remove vote).
     * @param currentVote The current vote value of the post (0 if no vote).
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(postId: String, value: Int, currentVote: Int): Result<Unit> {
        val finalValue = if (value == currentVote) 0 else value

        return postRepository.votePost(postId = postId, voteValue = finalValue)
    }
}