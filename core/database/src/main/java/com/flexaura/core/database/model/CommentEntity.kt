package com.flexaura.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Room entity representing a comment on a post in the local database.
 *
 * @property id Unique identifier for the comment.
 * @property postId Identifier of the post the comment belongs to.
 * @property userId Identifier of the user who wrote the comment.
 * @property content Text content of the comment.
 * @property createdAt Timestamp indicating when the comment was created.
 * @property authorUsername Author's username.
 * @property authorDisplayName Author's display name.
 * @property authorAvatarUrl URL to the author's avatar image.
 * @property authorTotalAura Total aura score of the author.
 */
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
