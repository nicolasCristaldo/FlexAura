package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing an extended user profile with follow check status
 * and utility conversion methods.
 *
 * @property id The unique identifier of the user.
 * @property username The unique username handle.
 * @property displayName The display name of the user.
 * @property imageUrl The profile image URL, or `null` if none exists.
 * @property bio The user's biography description, or `null` if none exists.
 * @property totalAura The total accumulated aura score of the user.
 * @property followersCount Total number of followers the user has.
 * @property followingCount Total number of accounts the user is following.
 * @property createdAt The timestamp of when the account was created.
 * @property isFollowingList List containing the follow relationship check record relative to the current user.
 */
@Serializable
data class UserProfileDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("total_aura") val totalAura: Int = 0,
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("created_at") val createdAt: String,
    @SerialName("follows") val isFollowingList: List<UserFollowCheckDto> = emptyList()
) {
    /**
     * Whether the current user is following this user.
     */
    val isCurrentUserFollowing: Boolean get() = isFollowingList.isNotEmpty()

    /**
     * Converts the user profile DTO to a regular user DTO.
     */
    fun toUserDto(): UserDto = UserDto(
        id = id,
        username = username,
        displayName = displayName,
        imageUrl = imageUrl,
        bio = bio,
        totalAura = totalAura,
        followersCount = followersCount,
        followingCount = followingCount,
        createdAt = createdAt
    )
}

/**
 * Data Transfer Object (DTO) representing a follow check identifier wrapper used in relational queries.
 *
 * @property id The unique identifier of the follow check record, or `null`.
 */
@Serializable
data class UserFollowCheckDto(@SerialName("id") val id: String? = null)
