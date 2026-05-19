package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostWithInteractionsDto(
    @SerialName("posts") val post: PostDto,
    @SerialName("votes") val userVote: List<UserVoteValueDto> = emptyList(),
    @SerialName("saves") val userSave: List<UserSaveIdDto> = emptyList()
) {
    val currentVoteValue: Int get() = userVote.firstOrNull()?.value ?: 0
    val isSaved: Boolean get() = userSave.isNotEmpty()
}

@Serializable
data class UserVoteValueDto(@SerialName("value") val value: Int)

@Serializable
data class UserSaveIdDto(@SerialName("id") val id: String)
