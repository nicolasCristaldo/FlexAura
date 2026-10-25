package com.flexaura.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Room entity representing a post in the local database.
 *
 * @property id Unique identifier for the post.
 * @property authorId Identifier of the user who wrote the post.
 * @property content Text content of the post.
 * @property imageUrl URL to the post's image.
 * @property createdAt Timestamp indicating when the post was created.
 * @property commentsCount Number of comments the post has.
 * @property savesCount Number of times the post has been saved.
 * @property totalAura Total aura score of the post.
 * @property currentUserVote Current user's vote value (e.g., 1 for upvote, -1 for downvote).
 * @property isSavedByUser Flag indicating if the current user has saved the post.
 */
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "comments_count") val commentsCount: Int,
    @ColumnInfo(name = "saves_count") val savesCount: Int,
    @ColumnInfo(name = "total_aura") val totalAura: Int = 0,
    @ColumnInfo(name = "current_user_vote") val currentUserVote: Int = 0,
    @ColumnInfo(name = "is_saved_by_user") val isSavedByUser: Boolean = false
)

/**
 * Room entity representing a relation between a post and its associated author in the local database.
 *
 * @property post The [PostEntity] representing the post.
 * @property author The [UserEntity] representing the author of the post.
 */
data class PostWithAuthorRelation(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity?
)
