package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) wrapping a user profile returned in follow/following relational queries.
 *
 * @property user The associated [UserDto] profile data.
 */
@Serializable
data class FollowResponseDto(
    @SerialName("users") val user: UserDto
)
