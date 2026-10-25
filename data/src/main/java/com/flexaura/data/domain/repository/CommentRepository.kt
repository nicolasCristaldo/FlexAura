package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface managing post comment data, synchronization, and atomic counter updates.
 *
 * Provides a reactive stream of local comments, and suspending methods to sync from remote,
 * create comments, and delete comments.
 */
interface CommentRepository {
    /**
     * Retrieves a reactive stream of comments associated with a specific post, mapped to domain models.
     *
     * @param postId The unique identifier of the post.
     * @return A [Flow] emitting a list of domain [Comment] items.
     */
    fun getCommentsFlow(postId: String): Flow<List<Comment>>

    /**
     * Fetches remote comments for a post and updates the local Room database cache.
     *
     * @param postId The unique identifier of the post whose comments are being synced.
     * @return A [Result] indicating success or containing an exception if synchronization fails.
     */
    suspend fun syncComments(postId: String): Result<Unit>

    /**
     * Creates a new comment on a post, updating local counters atomically with rollback on error.
     *
     * @param postId The unique identifier of the post being commented on.
     * @param content The text content of the comment.
     * @return A [Result] indicating success or containing an exception if creation fails.
     */
    suspend fun createComment(postId: String, content: String): Result<Unit>

    /**
     * Deletes a specific comment locally and remotely, updating the post's comment counter.
     *
     * @param commentId The unique identifier of the comment to delete.
     * @return A [Result] indicating success or containing an exception if deletion fails.
     */
    suspend fun deleteComment(commentId: String): Result<Unit>
}