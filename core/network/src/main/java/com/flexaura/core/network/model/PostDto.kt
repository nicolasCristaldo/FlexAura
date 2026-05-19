package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id") val id: String,
    @SerialName("author_id") val authorId: Int,
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
