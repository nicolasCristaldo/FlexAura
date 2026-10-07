package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.util.isValidDisplayName
import com.flexaura.data.domain.util.isValidEmail
import com.flexaura.data.domain.util.isValidPassword
import com.flexaura.data.domain.util.isValidUsername
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
        val trimmedUsername = username.trim()
        val trimmedDisplayName = displayName.trim()

        if (trimmedEmail.isBlank() || password.isBlank() || trimmedUsername.isBlank() || trimmedDisplayName.isBlank()) {
            return Result.failure(Exception("All fields must be filled"))
        }

        if (!username.isValidUsername()) {
            return Result.failure(Exception("Username must be between 3 and 15 characters."))
        }

        if (!trimmedDisplayName.isValidDisplayName()) {
            return Result.failure(Exception("Display name must be between 1 and 25 characters and cannot contain newlines."))
        }

        if (!trimmedEmail.isValidEmail()) {
            return Result.failure(Exception("Invalid email format."))
        }

        if (!password.isValidPassword()) {
            return Result.failure(Exception("Password must be at least 6 characters long."))
        }

        return repository.registerWithEmail(
            email = trimmedEmail,
            password = password,
            username = username.trim(),
            displayName = displayName.trim()
        )
    }
}