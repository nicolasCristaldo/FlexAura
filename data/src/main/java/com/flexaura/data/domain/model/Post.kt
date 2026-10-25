package com.flexaura.data.domain.model

/**
 * Domain model representing a post within the application.
 *
 * @property id The unique identifier of the post.
 * @property authorId The unique identifier of the user who created the post.
 * @property content The text content of the post, or `null` if it contains only media.
 * @property imageUrl The public URL of the attached image, or `null` if none exists.
 * @property createdAt The timestamp of when the post was created.
 * @property commentsCount Total number of comments associated with the post.
 * @property savesCount Total number of times the post has been saved/bookmarked.
 * @property totalAura The cumulative aura score for the post.
 * @property currentUserVote The current authenticated user's vote value on this post.
 * @property isSavedByUser Flag indicating whether the post is saved by the current user.
 * @property author The profile [User] who authored the post, or `null` if not loaded.
 */
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
    val isSavedByUser: Boolean,
    val author: User? = null
)