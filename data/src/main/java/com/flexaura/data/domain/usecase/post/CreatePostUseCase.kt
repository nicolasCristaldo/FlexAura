package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(content: String? = null, imageBytes: ByteArray? = null): Result<Unit> {
        val finalContent = if (content.isNullOrBlank()) null else content.trim()

        if (finalContent == null && imageBytes == null) {
            return Result.failure(IllegalArgumentException("The post cannot be empty"))
        }

        return repository.createPost(
            content = finalContent,
            imageBytes = imageBytes
        )
    }
}