package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val currentVoteValue: Int get() = userVote.firstOrNull()?.value ?: 0
    val isSaved: Boolean get() = userSave.isNotEmpty()
}

@Serializable
data class UserVoteValueDto(@SerialName("value") val value: Int)

@Serializable
data class UserSaveIdDto(@SerialName("id") val id: String)
