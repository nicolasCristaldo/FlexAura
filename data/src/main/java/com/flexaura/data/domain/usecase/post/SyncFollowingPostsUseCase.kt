package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class SyncFollowingPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int = 20): Result<Int> =
        repository.syncFollowingPosts(page, pageSize)
}