package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import com.flexaura.data.domain.util.isValidLength
import com.flexaura.data.domain.util.isValidUsername
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        username: String,
        displayName: String,
        imageBytes: ByteArray?,
        bio: String?
    ): Result<Unit> {
        return when {
            !username.isValidUsername() -> {
                Result.failure(IllegalArgumentException("Invalid username"))
            }
            !displayName.isValidLength(1, 25) -> {
                Result.failure(IllegalArgumentException("Invalid display name"))
            }
            bio?.isValidLength(0, 200) == false -> {
                Result.failure(IllegalArgumentException("Invalid bio"))
            }
            else -> {
                repository.updateProfile(
                    username = username.trim(),
                    displayName = displayName.trim(),
                    imageBytes = imageBytes,
                    bio = bio?.replace(Regex("\\n+"), " ")?.trim()
                )
            }
        }
    }
}