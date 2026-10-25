package com.flexaura.data.domain.usecase.post

import com.flexaura.data.domain.repository.PostRepository
import com.flexaura.data.domain.util.isValidLength
import javax.inject.Inject

/**
 * Use case for creating a new post.
 *
 * @property repository The repository for post-related operations.
 */
class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    /**
     * Creates a new post with the provided content and image bytes.
     *
     * @param content The content of the post.
     * @param imageBytes The image bytes of the post.
     * @return A [Result] indicating success or containing an exception if creation fails.
     */
    suspend operator fun invoke(content: String? = null, imageBytes: ByteArray? = null): Result<Unit> {
        val finalContent = if (content.isNullOrBlank()) null
            else content
                .replace(Regex("\\n{2,}"), "\n")
                .trim()

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