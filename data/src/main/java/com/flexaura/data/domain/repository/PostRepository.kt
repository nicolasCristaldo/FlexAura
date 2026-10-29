package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface managing post feeds, creation, local caching, synchronization, saving, and voting workflows.
 *
 * Provides reactive flows for global, author-specific, saved, and following timelines,
 * as well as suspending methods to sync from remote and perform post interactions.
 */
interface PostRepository {
    /**
     * Retrieves a reactive stream of global posts mapped to domain models.
     *
     * @return A [Flow] emitting a list of domain [Post] items.
     */
    fun getPostsFlow(): Flow<List<Post>>

    /**
     * Fetches a single post by its ID from the local database, mapped to a domain model.
     *
     * @param postId The unique identifier of the post.
     * @return The corresponding domain [Post], or `null` if not found.
     */
    suspend fun getPostById(postId: String): Post?

    /**
     * Retrieves a reactive stream of a single post by its ID mapped to a domain model.
     *
     * @param postId The unique identifier of the post.
     * @return A [Flow] emitting a single domain [Post] item.
     */
    fun getPostByIdFlow(postId: String): Flow<Post?>

    /**
     * Retrieves a reactive stream of posts authored by a specific user.
     *
     * @param userId The unique identifier of the author.
     * @return A [Flow] emitting a list of domain [Post] items authored by the user.
     */
    fun getPostsByAuthorFlow(userId: String): Flow<List<Post>>

    /**
     * Retrieves a reactive stream of posts saved/bookmarked by the current user.
     *
     * @return A [Flow] emitting a list of saved domain [Post] items.
     */
    fun getSavedPostsFlow(): Flow<List<Post>>

    /**
     * Retrieves a reactive stream of posts from accounts followed by the current user.
     *
     * @return A [Flow] emitting a list of domain [Post] items from followed users.
     */
    fun getFollowingPosts(): Flow<List<Post>>

    /**
     * Fetches global posts from the remote server and updates the local cache.
     *
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the number of fetched posts upon success, or an exception on failure.
     */
    suspend fun syncPosts(page: Int, pageSize: Int): Result<Int>

    /**
     * Fetches posts by a specific author from the remote server and updates the local cache.
     *
     * @param userId The unique identifier of the author.
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the number of fetched posts upon success, or an exception on failure.
     */
    suspend fun syncPostsByAuthor(userId: String, page: Int, pageSize: Int): Result<Int>

    /**
     * Fetches saved posts from the remote server and updates the local cache.
     *
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the number of fetched posts upon success, or an exception on failure.
     */
    suspend fun syncSavedPosts(page: Int, pageSize: Int): Result<Int>

    /**
     * Fetches posts from followed users from the remote server and updates the local cache.
     *
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing the number of fetched posts upon success, or an exception on failure.
     */
    suspend fun syncFollowingPosts(page: Int, pageSize: Int): Result<Int>

    /**
     * Creates a new post with optional text content and image bytes, uploading media if present and syncing locally.
     *
     * @param content The text content of the post, if any.
     * @param imageBytes The raw byte array of the attached image, if any.
     * @return A [Result] indicating success or containing an exception if creation fails.
     */
    suspend fun createPost(content: String?, imageBytes: ByteArray?): Result<Unit>

    /**
     * Toggles the save/bookmark state of a specific post locally and remotely.
     *
     * @param postId The unique identifier of the post.
     * @return A [Result] indicating success or containing an exception if toggling fails.
     */
    suspend fun toggleSavePost(postId: String): Result<Unit>

    /**
     * Deletes a post locally and remotely, including removing associated cloud media.
     *
     * @param postId The unique identifier of the post to delete.
     * @return A [Result] indicating success or containing an exception if deletion fails.
     */
    suspend fun deletePost(postId: String): Result<Unit>

    /**
     * Casts or updates a vote on a specific post, updating local counters and remote state.
     *
     * @param postId The unique identifier of the post.
     * @param voteValue The numeric vote value (e.g., upvote, downvote, or zero to remove).
     * @return A [Result] indicating success or containing an exception if voting fails.
     */
    suspend fun votePost(postId: String, voteValue: Int): Result<Unit>

    /**
     * Searches for posts matching a query string remotely, caches results, and returns matching domain models.
     *
     * @param query The search keyword.
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A [Result] containing a list of matching domain [Post] items upon success, or an exception on failure.
     */
    suspend fun searchPosts(query: String, page: Int, pageSize: Int): Result<List<Post>>
}