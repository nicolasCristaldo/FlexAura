package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.util.isValidEmail
import javax.inject.Inject

/**
 * Use case for logging in a user with email credentials.
 *
 * @property repository The repository for authentication-related operations.
 */
class LoginWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Logs in a user with the provided email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] indicating success or containing an exception if login fails.
     */
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }

        if (!trimmedEmail.isValidEmail()) {
            return Result.failure(Exception("Invalid email format"))
        }

        return repository.loginWithEmail(email = trimmedEmail, password = password)
    }
}