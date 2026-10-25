package com.flexaura.data.domain.model

/**
 * Domain model representing a user profile in the application.
 *
 * @property id The unique identifier of the user.
 * @property username The unique username handle.
 * @property displayName The display name of the user.
 * @property imageUrl The profile image URL, or `null` if none exists.
 * @property bio The user's biography description, or `null` if none exists.
 * @property totalAura The total accumulated aura score of the user.
 * @property followersCount Total number of followers the user has.
 * @property followingCount Total number of accounts the user is following.
 * @property isFollowedByCurrentUser Flag indicating whether the current user is following this profile.
 */
data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val imageUrl: String?,
    val bio: String?,
    val totalAura: Int,
    val followersCount: Int,
    val followingCount: Int,
    val isFollowedByCurrentUser: Boolean = false
)