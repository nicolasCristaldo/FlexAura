package com.flexaura.core.network.datasource.database.post

import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto

/**
 * Interface defining remote post management, voting, saving, and interaction data operations using Supabase.
 *
 * Provides suspending methods for fetching global feeds, author-specific posts, search queries,
 * following timelines, bookmarking, and voting workflows.
 */
interface PostRemoteDataSource {
    // Posts
    /**
     * Fetches a single post DTO by its unique identifier.
     *
     * @param postId The unique identifier of the post.
     * @return The corresponding [PostDto], or `null` if the post is not found.
     */
    suspend fun getPost(postId: String): PostDto?

    /**
     * Fetches a paginated list of global posts with user interactions (votes, saves, author info).
     *
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A list of [PostWithInteractionsDto] items.
     */
    suspend fun getPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>

    /**
     * Creates a new post on behalf of the currently authenticated user.
     *
     * @param id The unique identifier for the post.
     * @param content The text content of the post, if any.
     * @param imageUrl The image URL attached to the post, if any.
     * @return The created [PostDto].
     */
    suspend fun createPost(id: String, content: String?, imageUrl: String?): PostDto

    /**
     * Deletes a specific post from the remote database by its ID.
     *
     * @param postId The unique identifier of the post to delete.
     */
    suspend fun deletePost(postId: String)

    /**
     * Fetches a paginated list of posts authored by a specific user with interaction details.
     *
     * @param authorId The unique identifier of the author.
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A list of [PostWithInteractionsDto] items.
     */
    suspend fun getPostsByAuthor(authorId: String, page: Int, pageSize: Int): List<PostWithInteractionsDto>

    /**
     * Searches for posts matching a query string in their content with pagination support.
     *
     * @param query The search keyword.
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A list of matching [PostWithInteractionsDto] items.
     */
    suspend fun searchPosts(query: String, page: Int, pageSize: Int): List<PostWithInteractionsDto>

    /**
     * Fetches a paginated feed of posts from users followed by the current authenticated user.
     *
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A list of [PostWithInteractionsDto] items from followed accounts.
     */
    suspend fun getFollowingPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>

    // Votes
    /**
     * Creates or updates a vote cast by the current user on a specific post.
     *
     * @param postId The unique identifier of the post being voted on.
     * @param value The vote value (e.g., upvote or downvote representation).
     */
    suspend fun upsertVote(postId: String, value: Int)

    /**
     * Removes the current user's vote from a specific post.
     *
     * @param postId The unique identifier of the post.
     */
    suspend fun deleteVote(postId: String)

    // Saves
    /**
     * Bookmarks/saves a specific post for the current authenticated user.
     *
     * @param postId The unique identifier of the post to save.
     */
    suspend fun savePost(postId: String)

    /**
     * Removes a post from the current user's saved items.
     *
     * @param postId The unique identifier of the post to unsave.
     */
    suspend fun unsavePost(postId: String)

    /**
     * Fetches a paginated list of posts saved/bookmarked by the current authenticated user.
     *
     * @param page The pagination page index.
     * @param pageSize The number of posts per page.
     * @return A list of saved [PostWithInteractionsDto] items.
     */
    suspend fun getSavedPosts(page: Int, pageSize: Int): List<PostWithInteractionsDto>
}