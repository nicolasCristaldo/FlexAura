package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowResponseDto(
    @SerialName("users") val user: UserDto
)
