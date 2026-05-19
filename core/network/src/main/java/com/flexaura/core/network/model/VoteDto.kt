package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoteDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("post_id") val postId: String,
    @SerialName("value") val value: Int,
    @SerialName("created_at") val createdAt: String? = null
)
