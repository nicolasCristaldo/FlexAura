package com.flexaura.core.network.datasource.auth

import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining remote authentication operations using Supabase.
 *
 * Provides reactive session status monitoring and methods for email registration, authentication,
 * Google sign-in, session termination, and account deletion.
 */
interface AuthRemoteDataSource {
    /** A reactive stream emitting the current authentication [SessionStatus] changes. */
    val sessionStatus: Flow<SessionStatus>

    /**
     * Registers a new user with email and password, including custom metadata such as username and display name.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param username The unique username handle.
     * @param displayName The user's display name.
     * @return The unique identifier of the newly created user.
     */
    suspend fun signUpWithEmail(email: String, password: String, username: String, displayName: String): String

    /**
     * Authenticates an existing user using their email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    suspend fun signInWithEmail(email: String, password: String)

    /**
     * Authenticates a user using a Google OAuth ID token.
     *
     * @param idToken The Google ID token obtained from the authentication provider.
     */
    suspend fun signInWithGoogle(idToken: String)

    /**
     * Terminates the current active user session.
     */
    suspend fun signOut()

    /**
     * Deletes the currently authenticated user's account via a remote database RPC call and clears the session.
     */
    suspend fun deleteAccount()
}