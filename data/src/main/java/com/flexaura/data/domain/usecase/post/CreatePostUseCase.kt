package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import com.flexaura.data.domain.util.isValidLength
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(content: String? = null, imageBytes: ByteArray? = null): Result<Unit> {
        val finalContent = if (content.isNullOrBlank()) null else content.trim()

        if (finalContent == null && imageBytes == null) {
            return Result.failure(IllegalArgumentException("The post cannot be empty"))
        }

        finalContent?.let {
            if (!it.isValidLength(1, 280)) {
                return Result.failure(IllegalArgumentException("The post cannot be longer than 280 characters"))
            }
        }

        return repository.createPost(
            content = finalContent,
            imageBytes = imageBytes
        )
    }
}