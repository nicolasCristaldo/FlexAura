package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a comment resource received from or sent to the remote backend.
 *
 * @property id The unique identifier of the comment.
 * @property postId The unique identifier of the post the comment belongs to.
 * @property authorId The unique identifier of the user who authored the comment.
 * @property content The text content of the comment.
 * @property createdAt The ISO-8601 timestamp of when the comment was created, or `null`.
 */
@Serializable
data class CommentDto(
    @SerialName("id") val id: String,
    @SerialName("post_id") val postId: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String? = null
)