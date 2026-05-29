package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.util.isValidEmail
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
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