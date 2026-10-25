package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for syncing a user's profile.
 *
 * @property repository The repository for user-related operations.
 */
class SyncUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    /**
     * Synchronizes a user's profile.
     *
     * @param userId The ID of the user.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(userId: String): Result<Unit> =
        repository.syncUserProfile(userId = userId)
}