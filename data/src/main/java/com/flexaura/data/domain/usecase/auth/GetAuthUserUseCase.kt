package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.repository.AuthState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the current authentication state of the user.
 *
 * @property repository The repository for authentication-related operations.
 */
class GetAuthUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /**
     * Retrieves the current authentication state of the user.
     *
     * @return A [Flow] emitting the current [AuthState].
     */
    operator fun invoke(): Flow<AuthState> = repository.authState
}