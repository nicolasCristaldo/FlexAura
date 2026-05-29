package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        if (idToken.isBlank()) {
            return Result.failure(Exception("ID token is invalid"))
        }

        return repository.loginWithGoogle(idToken = idToken)
    }
}