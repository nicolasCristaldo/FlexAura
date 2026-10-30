package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import com.flexaura.data.domain.util.isValidLength
import com.flexaura.data.domain.util.isValidUsername
import javax.inject.Inject

/**
 * Use case for updating the user's profile.
 *
 * @property repository The repository for user-related operations.
 */
class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    /**
     * Updates the user's profile.
     *
     * @param username The new username.
     * @param displayName The new display name.
     * @param imageBytes The new image bytes.
     * @param bio The new bio.
     * @param isImageRemoved Whether the image is removed.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(
        username: String,
        displayName: String,
        imageBytes: ByteArray?,
        bio: String?,
        isImageRemoved: Boolean
    ): Result<Unit> {
        val finalBio = if (bio.isNullOrBlank()) null
            else bio.replace(Regex("\\n+"), " ").trim()

        return when {
            !username.isValidUsername() -> {
                Result.failure(IllegalArgumentException("Invalid username"))
            }
            !displayName.isValidLength(1, 25) -> {
                Result.failure(IllegalArgumentException("Invalid display name"))
            }
            finalBio?.isValidLength(0, 200) == false -> {
                Result.failure(IllegalArgumentException("Invalid bio"))
            }
            else -> {
                repository.updateProfile(
                    username = username.trim(),
                    displayName = displayName.trim(),
                    imageBytes = imageBytes,
                    bio = finalBio,
                    isImageRemoved = isImageRemoved
                )
            }
        }
    }
}