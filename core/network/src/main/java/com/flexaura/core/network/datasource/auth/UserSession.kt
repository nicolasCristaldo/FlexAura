package com.flexaura.core.network.datasource.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    val currentUserId: String?
        get() = supabaseClient.auth.currentUserOrNull()?.id

    fun requireCurrentUserId(): String =
        currentUserId ?: throw Exception("No user logged in")
}