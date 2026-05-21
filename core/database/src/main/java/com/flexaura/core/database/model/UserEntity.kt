package com.flexaura.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "bio") val bio: String?,
    @ColumnInfo(name = "total_aura") val totalAura: Int,
    @ColumnInfo(name = "followers_count") val followersCount: Int,
    @ColumnInfo(name = "following_count") val followingCount: Int,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "is_followed_by_current_user") val isFollowedByCurrentUser: Boolean = false
)
