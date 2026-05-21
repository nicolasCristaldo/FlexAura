package com.flexaura.data.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val imageUrl: String?,
    val bio: String?,
    val totalAura: Int,
    val followersCount: Int,
    val followingCount: Int,
    val isFollowedByCurrentUser: Boolean = false
)
