package com.flexaura.data.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun registerWithEmail(email: String, password: String, username: String, displayName: String): Result<Unit>
    suspend fun loginWithEmail(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}

sealed interface AuthState {
    object Loading: AuthState
    data class Authenticated(val userId: String): AuthState
    object Unauthenticated: AuthState
}