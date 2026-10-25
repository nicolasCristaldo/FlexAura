package com.flexaura.core.network.datasource.database.comment

import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.CommentWithAuthorDto

/**
 * Interface defining remote data operations for post comments using Supabase.
 *
 * Provides suspending methods to fetch, create, and delete comments on the remote backend.
 */
interface CommentRemoteDataSource {
    /**
     * Fetches a list of comments with author details for a specific post.
     *
     * @param postId The unique identifier of the post.
     * @return A list of [CommentWithAuthorDto] associated with the post.
     */
    suspend fun getComments(postId: String): List<CommentWithAuthorDto>

    /**
     * Creates a new comment for a post on behalf of the currently authenticated user.
     *
     * @param postId The unique identifier of the post being commented on.
     * @param content The text content of the comment.
     * @return The created [CommentDto].
     */
    suspend fun createComment(postId: String, content: String): CommentDto

    /**
     * Deletes a specific comment from the remote database by its ID.
     *
     * @param commentId The unique identifier of the comment to delete.
     */
    suspend fun deleteComment(commentId: String)
}