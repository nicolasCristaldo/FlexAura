package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(content: String? = null, imageUrl: String? = null): Result<Unit> {
        val finalContent = if (content.isNullOrBlank()) null else content.trim()
        val finalImageUrl = if (imageUrl.isNullOrBlank()) null else imageUrl.trim()

        if (finalContent == null && finalImageUrl == null) {
            return Result.failure(IllegalArgumentException("The post cannot be empty"))
        }

        return repository.createPost(
            content = finalContent,
            imageUrl = finalImageUrl
        )
    }
}