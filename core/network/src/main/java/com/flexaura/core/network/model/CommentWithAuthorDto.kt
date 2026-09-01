package com.flexaura.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentWithAuthorDto(
    @SerialName("id") val id: String,
    @SerialName("post_id") val postId: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("users") val author: UserDto
)
