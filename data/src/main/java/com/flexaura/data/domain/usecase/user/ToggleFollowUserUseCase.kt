package com.flexaura.data.domain.usecase.user

import com.flexaura.data.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for toggling the follow status of a user.
 *
 * @property repository The repository for user-related operations.
 */
class ToggleFollowUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    /**
     * Toggles the follow status of a user.
     *
     * @param targetUserId The ID of the user to toggle the follow status for.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend operator fun invoke(targetUserId: String): Result<Unit> =
        repository.toggleFollowUser(targetUserId = targetUserId)
}