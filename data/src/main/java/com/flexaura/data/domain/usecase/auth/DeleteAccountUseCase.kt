package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for deleting the current user's account.
 *
 * @property repository The repository for authentication-related operations.
 */
class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Deletes the current user's account.
     *
     * @return A [Result] indicating success or containing an exception if deletion fails.
     */
    suspend operator fun invoke(): Result<Unit> = repository.deleteAccount()
}