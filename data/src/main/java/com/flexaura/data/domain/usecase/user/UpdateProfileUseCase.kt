package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        username: String,
        displayName: String,
        imageBytes: ByteArray?,
        bio: String?
    ): Result<Unit> =
        repository.updateProfile(
            username = username,
            displayName = displayName,
            imageBytes = imageBytes,
            bio = bio
        )
}