package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class SyncPostsByAuthorUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(userId: String, page: Int = 1, pageSize: Int = 20): Result<Unit> =
        repository.syncPostsByAuthor(userId, page, pageSize)
}