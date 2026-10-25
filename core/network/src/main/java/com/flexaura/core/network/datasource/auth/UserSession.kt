package com.flexaura.core.network.datasource.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton helper class for managing and checking the current authenticated user's session state.
 *
 * @property supabaseClient The Supabase client instance used to query current user details.
 */
@Singleton
class UserSession @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    /**
     * Retrieves the unique identifier of the currently authenticated user, or `null` if no user is logged in.
     */
    val currentUserId: String?
        get() = supabaseClient.auth.currentUserOrNull()?.id

    /**
     * Retrieves the current user's ID or throws an exception if the user is unauthenticated.
     *
     * @return The unique identifier of the logged-in user.
     * @throws Exception If no user session is active.
     */
    fun requireCurrentUserId(): String =
        currentUserId ?: throw Exception("No user logged in")
}