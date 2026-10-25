package com.flexaura.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.PostWithAuthorRelation
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing local [PostEntity] data and its relationships with authors in the Room database.
 *
 * Provides reactive streams and suspending queries for feeds, user-specific posts, saved items,
 * local counter updates (votes, saves, comments), and post persistence.
 */
@Dao
interface PostDao {
    /**
     * Retrieves a reactive stream of all posts ordered by creation date in descending order.
     *
     * @return A [Flow] emitting lists of [PostWithAuthorRelation] representing all posts with their respective authors.
     */
    @Transaction
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getPostsFlow(): Flow<List<PostWithAuthorRelation>>

    /**
     * Retrieves a reactive stream of posts written by authors followed by the current user, ordered by creation date.
     *
     * @return A [Flow] emitting lists of [PostWithAuthorRelation] from followed users.
     */
    @Transaction
    @Query("""
        SELECT posts.* FROM posts 
        INNER JOIN users ON posts.author_id = users.id
        WHERE users.is_followed_by_current_user = 1
        ORDER BY posts.created_at DESC
    """)
    fun getFollowingPostsFlow(): Flow<List<PostWithAuthorRelation>>

    /**
     * Retrieves a reactive stream of posts saved by the current user, ordered by creation date.
     *
     * @return A [Flow] emitting lists of bookmarked [PostWithAuthorRelation] items.
     */
    @Transaction
    @Query("""
        SELECT * FROM posts 
        WHERE is_saved_by_user = 1
        ORDER BY created_at DESC
    """)
    fun getSavedPostsFlow(): Flow<List<PostWithAuthorRelation>>

    /**
     * Fetches a single post along with its author relation by its unique identifier.
     *
     * @param postId The unique identifier of the post to retrieve.
     * @return The corresponding [PostWithAuthorRelation], or `null` if it does not exist locally.
     */
    @Transaction
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostWithAuthorRelation?

    /**
     * Retrieves a reactive stream of posts authored by a specific user, ordered by creation date descending.
     *
     * @param userId The unique identifier of the author.
     * @return A [Flow] emitting a list of [PostWithAuthorRelation] belonging to the specified user.
     */
    @Transaction
    @Query("SELECT * FROM posts WHERE author_id = :userId ORDER BY created_at DESC")
    fun getPostsByAuthorFlow(userId: String): Flow<List<PostWithAuthorRelation>>

    /**
     * Fetches a collection of posts with their author relations based on a list of post IDs.
     *
     * @param postIds A list of unique post identifiers to query.
     * @return A list of [PostWithAuthorRelation] matching the provided IDs.
     */
    @Transaction
    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    fun getPostsWithAuthorByIds(postIds: List<String>): List<PostWithAuthorRelation>

    /**
     * Inserts or updates a list of post entities in the local database.
     *
     * @param posts The list of [PostEntity] items to upsert.
     */
    @Upsert
    suspend fun upsertPosts(posts: List<PostEntity>)

    /**
     * Updates the current user's vote value and total aura score for a specific post locally.
     *
     * @param postId The unique identifier of the post.
     * @param voteValue The new vote value cast by the user.
     * @param newTotalAura The updated cumulative aura score.
     */
    @Query("""
        UPDATE posts
        SET current_user_vote = :voteValue, total_aura = :newTotalAura
        WHERE id = :postId
    """)
    suspend fun updateLocalVote(postId: String, voteValue: Int, newTotalAura: Int)

    /**
     * Updates the bookmarked/saved state and total saves count for a specific post locally.
     *
     * @param postId The unique identifier of the post.
     * @param isSaved Boolean flag indicating whether the post is saved by the user.
     * @param savesCount The updated total saves count.
     */
    @Query("""
        UPDATE posts
        SET is_saved_by_user = :isSaved, saves_count = :savesCount 
        WHERE id = :postId
    """)
    suspend fun updateLocalSaveState(postId: String, isSaved: Boolean, savesCount: Int)

    /**
     * Incrementally updates the comments count for a specific post locally.
     *
     * @param postId The unique identifier of the post.
     * @param increment The integer delta to add (e.g., `1` for a new comment, `-1` for a deleted one).
     */
    @Query(
        """
        UPDATE posts
        SET comments_count = comments_count + :increment
        WHERE id = :postId
    """
    )
    suspend fun updateLocalCommentsCount(postId: String, increment: Int)

    /**
     * Deletes a specific post from the local database by its ID.
     *
     * @param postId The unique identifier of the post to delete.
     */
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    /**
     * Deletes all cached posts from the local database table.
     */
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}