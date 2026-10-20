package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class SyncSavedPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(page: Int = 1, pageSize: Int = 20): Result<Int> =
        repository.syncSavedPosts(page, pageSize)
}