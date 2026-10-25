package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging out the current user.
 *
 * @property repository The repository for authentication-related operations.
 */
class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Logs out the current user.
     *
     * @return A [Result] indicating success or containing an exception if logout fails.
     */
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}