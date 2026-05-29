package com.flexaura.data.domain.usecase.auth

import com.flexaura.data.domain.repository.AuthRepository
import com.flexaura.data.domain.repository.AuthState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthState> = repository.authState
}