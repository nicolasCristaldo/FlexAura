package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class SyncPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int = 20): Result<Unit> =
        repository.syncPosts(page = page, pageSize = pageSize)
}