package com.flexaura.data.domain.model

/**
 * Domain model representing a comment on a post.
 *
 * @property id The unique identifier of the comment.
 * @property postId The unique identifier of the post this comment belongs to.
 * @property authorId The unique identifier of the user who authored the comment.
 * @property content The text content of the comment.
 * @property createdAt The timestamp of when the comment was created.
 * @property authorUsername The username of the comment author.
 * @property authorDisplayName The display name of the comment author.
 * @property authorAvatarUrl The avatar image URL of the comment author, or `null`.
 * @property authorTotalAura The total accumulated aura score of the comment author.
 */
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