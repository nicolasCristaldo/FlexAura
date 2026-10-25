package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging in a user with Google credentials.
 *
 * @property repository The repository for authentication-related operations.
 */
class LoginWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Logs in a user with the provided Google ID token.
     *
     * @param idToken The Google ID token.
     * @return A [Result] indicating success or containing an exception if login fails.
     */
    suspend operator fun invoke(idToken: String): Result<Unit> {
        if (idToken.isBlank()) {
            return Result.failure(Exception("ID token is invalid"))
        }

        return repository.loginWithGoogle(idToken = idToken)
    }
}