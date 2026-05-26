package com.flexaura.data.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val authorId: String,
    val content: String,
    val createdAt: String,
    val authorUsername: String,
    val authorDisplayName: String,
    val authorAvatarUrl: String?,
    val authorTotalAura: Int
)