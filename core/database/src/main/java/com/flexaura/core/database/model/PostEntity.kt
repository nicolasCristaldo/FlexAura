package com.flexaura.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    val content: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "comments_count") val commentsCount: Int,
    @ColumnInfo(name = "saves_count") val savesCount: Int,
    @ColumnInfo(name = "total_aura") val totalAura: Int = 0,
    @ColumnInfo(name = "current_user_vote") val currentUserVote: Int = 0,
    @ColumnInfo(name = "is_saved_by_user") val isSavedByUser: Boolean = false
)
