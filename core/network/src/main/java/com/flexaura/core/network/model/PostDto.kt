package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing raw post data received from the remote backend.
 *
 * @property id The unique identifier of the post.
 * @property authorId The unique identifier of the user who created the post.
 * @property content The text content of the post, or `null` if it contains only media.
 * @property imageUrl The public URL of the attached image, or `null` if none exists.
 * @property createdAt The timestamp of when the post was created.
 * @property commentsCount Total number of comments associated with the post.
 * @property savesCount Total number of times the post has been bookmarked.
 * @property votesMinus1 Count of negative votes or downvote weights.
 * @property votes1 Count of tier 1 votes/aura points.
 * @property votes3 Count of tier 3 votes/aura points.
 * @property votes5 Count of tier 5 votes/aura points.
 */
@Serializable
data class PostDto(
    @SerialName("id") val id: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("content") val content: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("comments_count") val commentsCount: Int = 0,
    @SerialName("saves_count") val savesCount: Int = 0,
    @SerialName("votes_minus_1") val votesMinus1: Int = 0,
    @SerialName("votes_1") val votes1: Int = 0,
    @SerialName("votes_3") val votes3: Int = 0,
    @SerialName("votes_5") val votes5: Int = 0
)
