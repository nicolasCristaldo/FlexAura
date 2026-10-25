package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a follow relationship between users.
 *
 * @property id The unique identifier of the follow record, or `null` if not generated yet.
 * @property followerId The unique identifier of the user initiating the follow action.
 * @property followingId The unique identifier of the user being followed.
 * @property createdAt The ISO-8601 timestamp of when the relationship was established, or `null`.
 */
@Serializable
data class FollowDto(
    @SerialName("id") val id: String? = null,
    @SerialName("follower_id") val followerId: String,
    @SerialName("following_id") val followingId: String,
    @SerialName("created_at") val createdAt: String? = null
)
