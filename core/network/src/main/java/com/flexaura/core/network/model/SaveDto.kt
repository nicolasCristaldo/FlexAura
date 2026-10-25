package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a bookmark/save record between a user and a post.
 *
 * @property id The unique identifier of the save record, or `null` if not generated yet.
 * @property userId The unique identifier of the user who saved the post.
 * @property postId The unique identifier of the post being saved.
 * @property createdAt The timestamp of when the post was saved, or `null`.
 */
@Serializable
data class SaveDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("post_id") val postId: String,
    @SerialName("created_at") val createdAt: String? = null
)
