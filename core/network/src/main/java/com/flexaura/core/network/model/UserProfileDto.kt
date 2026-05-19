package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("users") val user: UserDto,
    @SerialName("follows") val isFollowingList: List<UserFollowCheckDto> = emptyList()
) {
    val isCurrentUserFollowing: Boolean get() = isFollowingList.isNotEmpty()
}

@Serializable
data class UserFollowCheckDto(@SerialName("id") val id: String)
