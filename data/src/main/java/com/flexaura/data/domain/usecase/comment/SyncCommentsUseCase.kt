package com.flexaura.data.domain.usecase.comment

import com.flexaura.data.domain.repository.CommentRepository
import javax.inject.Inject

class SyncCommentsUseCase @Inject constructor(
    private val repository: CommentRepository
) {
    suspend operator fun invoke(postId: String): Result<Unit> =
        repository.syncComments(postId = postId)
}