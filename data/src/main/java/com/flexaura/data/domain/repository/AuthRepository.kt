package com.flexaura.data.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface managing authentication state and operations.
 *
 * Provides a reactive stream of authentication states and suspending methods for email registration,
 * email and Google authentication, session termination, and account deletion.
 */
interface AuthRepository {
    /** A reactive stream emitting the current [AuthState] changes. */
    val authState: Flow<AuthState>

    /**
     * Registers a new user with email, password, username, and display name.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param username The unique username handle.
     * @param displayName The user's display name.
     * @return A [Result] indicating success or containing an exception if registration fails.
     */
    suspend fun registerWithEmail(email: String, password: String, username: String, displayName: String): Result<Unit>

    /**
     * Authenticates an existing user with email and password, syncing local user profile data.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] indicating success or containing an exception if login fails.
     */
    suspend fun loginWithEmail(email: String, password: String): Result<Unit>

    /**
     * Authenticates a user using a Google ID token, syncing local user profile data.
     *
     * @param idToken The Google OAuth ID token.
     * @return A [Result] indicating success or containing an exception if login fails.
     */
    suspend fun loginWithGoogle(idToken: String): Result<Unit>

    /**
     * Terminates the current session and clears local cached user and post data.
     *
     * @return A [Result] indicating success or containing an exception if logout fails.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Deletes the user account remotely and clears local cached user and post data.
     *
     * @return A [Result] indicating success or containing an exception if account deletion fails.
     */
    suspend fun deleteAccount(): Result<Unit>
}

/**
 * Represents the current authentication state of the application.
 */
sealed interface AuthState {
    /** Indicates that the authentication state is currently loading or being verified. */
    object Loading: AuthState

    /**
     * Indicates that a user is successfully authenticated.
     *
     * @property userId The unique identifier of the authenticated user.
     */
    data class Authenticated(val userId: String): AuthState

    /** Indicates that no user session is active. */
    object Unauthenticated: AuthState
}