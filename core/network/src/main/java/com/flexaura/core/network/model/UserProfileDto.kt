package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val isCurrentUserFollowing: Boolean get() = isFollowingList.isNotEmpty()

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

@Serializable
data class UserFollowCheckDto(@SerialName("id") val id: String)
