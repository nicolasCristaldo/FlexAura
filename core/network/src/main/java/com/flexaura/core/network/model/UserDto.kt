package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a core user profile resource received from or sent to the remote backend.
 *
 * @property id The unique identifier of the user.
 * @property username The unique username handle.
 * @property displayName The display name of the user.
 * @property imageUrl The profile image URL, or `null` if none exists.
 * @property bio The user's biography description, or `null` if none exists.
 * @property totalAura The total accumulated aura score of the user.
 * @property follows List containing follow check status records, if any.
 * @property followersCount Total number of followers the user has.
 * @property followingCount Total number of accounts the user is following.
 * @property createdAt The timestamp of when the account was created.
 */
@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("total_aura") val totalAura: Int = 0,
    @SerialName("follows") val follows: List<UserFollowCheckDto> = emptyList(),
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("created_at") val createdAt: String
)