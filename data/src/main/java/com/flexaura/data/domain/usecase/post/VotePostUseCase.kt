package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class VotePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, value: Int, currentVote: Int): Result<Unit> {
        val finalValue = if (value == currentVote) 0 else value

        return postRepository.votePost(postId = postId, voteValue = finalValue)
    }
}