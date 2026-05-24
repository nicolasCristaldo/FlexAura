package com.flexaura.data.domain.model

data class Post(
    val id: String,
    val authorId: String,
    val content: String?,
    val imageUrl: String?,
    val createdAt: String,
    val commentsCount: Int,
    val savesCount: Int,
    val totalAura: Int,
    val currentUserVote: Int,
    val isSavedByUser: Boolean
)