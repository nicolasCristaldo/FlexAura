package com.flexaura.core.network.datasource.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
): AuthRemoteDataSource {
    override val sessionStatus: Flow<SessionStatus>
        get() = supabaseClient.auth.sessionStatus

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): String {
        val user = supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                put("username", username)
                put("display_name", displayName)
            }
        }

        return user?.id ?: throw Exception("Sign up failed: User not found")
    }

    override suspend fun signInWithEmail(email: String, password: String) =
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

    override suspend fun signInWithGoogle(idToken: String) =
        supabaseClient.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
        }

    override suspend fun signOut() = supabaseClient.auth.signOut()
}