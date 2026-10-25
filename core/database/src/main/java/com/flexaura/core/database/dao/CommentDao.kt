package com.flexaura.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flexaura.core.database.model.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing local [CommentEntity] data operations in the Room database.
 *
 * Provides reactive data streams for comments associated with specific posts, along with
 * methods for single/bulk insertion and deletion queries.
 */
@Dao
interface CommentDao {
    /**
     * Retrieves a reactive stream of all comments belonging to a specific post.
     *
     * @param postId The unique identifier of the post whose comments are being observed.
     * @return A [Flow] emitting a list of [CommentEntity] objects ordered or updated in real-time.
     */
    @Query("SELECT * FROM comments WHERE post_id = :postId")
    fun getCommentsByPostId(postId: String): Flow<List<CommentEntity>>

    /**
     * Fetches a single comment entity by its unique identifier.
     *
     * @param commentId The unique identifier of the comment.
     * @return The corresponding [CommentEntity], or `null` if it does not exist locally.
     */
    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: String): CommentEntity?

    /**
     * Inserts a single comment into the local database, replacing it if a conflict occurs.
     *
     * @param comment The [CommentEntity] to be inserted or updated.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    /**
     * Inserts a list of comments into the local database in bulk, replacing any conflicts.
     *
     * @param comments The list of [CommentEntity] items to upsert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    /**
     * Deletes a specific comment from the database by its ID.
     *
     * @param commentId The unique identifier of the comment to delete.
     */
    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)

    /**
     * Deletes all comments associated with a specific post ID.
     *
     * @param postId The unique identifier of the post whose comments should be purged.
     */
    @Query("DELETE FROM comments WHERE post_id = :postId")
    suspend fun deleteCommentsByPostId(postId: String)
}