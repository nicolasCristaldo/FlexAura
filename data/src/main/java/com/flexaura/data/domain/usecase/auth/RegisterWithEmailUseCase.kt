package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.util.isValidEmail
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): Result<Unit> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank() || password.isBlank() || username.isBlank() || displayName.isBlank()) {
            return Result.failure(Exception("All fields must be filled"))
        }

        if (!trimmedEmail.isValidEmail()) {
            return Result.failure(Exception("Invalid email format"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters long"))
        }

        return repository.registerWithEmail(
            email = trimmedEmail,
            password = password,
            username = username.trim(),
            displayName = displayName.trim()
        )
    }
}