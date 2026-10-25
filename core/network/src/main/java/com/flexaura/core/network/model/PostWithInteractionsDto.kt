package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a post bundled with its complete interaction details,
 * including votes, save states, and author profile information.
 *
 * @property id The unique identifier of the post.
 * @property authorId The unique identifier of the user who created the post.
 * @property content The text content of the post, or `null` if it contains only media.
 * @property imageUrl The public URL of the attached image, or `null` if none exists.
 * @property createdAt The ISO-8601 timestamp of when the post was created.
 * @property commentsCount Total number of comments associated with the post.
 * @property savesCount Total number of times the post has been saved.
 * @property votes1 Count of tier 1 votes/aura points.
 * @property votes3 Count of tier 3 votes/aura points.
 * @property votes5 Count of tier 5 votes/aura points.
 * @property votesMinus1 Count of negative votes or downvote weights.
 * @property userVote List containing the current user's vote record for this post, if any.
 * @property userSave List containing the current user's save record for this post, if any.
 * @property author The profile [UserDto] of the user who authored the post, or `null`.
 */
@Serializable
data class PostWithInteractionsDto(
    @SerialName("id") val id: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("content") val content: String?,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("created_at") val createdAt: String,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("saves_count") val savesCount: Int,
    @SerialName("votes_1") val votes1: Int,
    @SerialName("votes_3") val votes3: Int,
    @SerialName("votes_5") val votes5: Int,
    @SerialName("votes_minus_1") val votesMinus1: Int,
    @SerialName("votes") val userVote: List<UserVoteValueDto> = emptyList(),
    @SerialName("saves") val userSave: List<UserSaveIdDto> = emptyList(),
    @SerialName("users") val author: UserDto? = null
) {
    /**
     * The current user's vote value for this post.
     */
    val currentVoteValue: Int get() = userVote.firstOrNull()?.value ?: 0

    /**
     * Whether the current user has saved this post.
     */
    val isSaved: Boolean get() = userSave.isNotEmpty()
}

/**
 * Data Transfer Object (DTO) representing a user's vote value wrapper for a post.
 *
 * @property value The numeric vote value cast by the user.
 */
@Serializable
data class UserVoteValueDto(@SerialName("value") val value: Int)

/**
 * Data Transfer Object (DTO) representing a user's save record identifier wrapper.
 *
 * @property id The unique identifier of the save record.
 */
@Serializable
data class UserSaveIdDto(@SerialName("id") val id: String)
