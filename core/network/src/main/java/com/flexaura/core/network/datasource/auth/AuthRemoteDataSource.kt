package com.flexaura.core.network.datasource.auth

import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow

interface AuthRemoteDataSource {
    val sessionStatus: Flow<SessionStatus>

    suspend fun signUpWithEmail(email: String, password: String, username: String, displayName: String): String
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signOut()
}