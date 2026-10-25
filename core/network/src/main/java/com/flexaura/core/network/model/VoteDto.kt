package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a vote record cast by a user on a post.
 *
 * @property id The unique identifier of the vote record, or `null` if not generated yet.
 * @property userId The unique identifier of the user who cast the vote.
 * @property postId The unique identifier of the post being voted on.
 * @property value The numeric value of the vote.
 * @property createdAt The ISO-8601 timestamp of when the vote was cast, or `null`.
 */
@Serializable
data class VoteDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("post_id") val postId: String,
    @SerialName("value") val value: Int,
    @SerialName("created_at") val createdAt: String? = null
)
