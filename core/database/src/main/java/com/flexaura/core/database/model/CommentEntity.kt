package com.flexaura.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "post_id") val postId: String,
    @ColumnInfo(name = "author_id") val userId: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "author_username") val authorUsername: String,
    @ColumnInfo(name = "author_display_name") val authorDisplayName: String,
    @ColumnInfo(name = "author_avatar_url") val authorAvatarUrl: String?,
    @ColumnInfo(name = "author_total_aura") val authorTotalAura: Int,
)
