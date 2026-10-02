package com.flexaura.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.database.model.PostWithAuthorRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Transaction
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getPostsFlow(): Flow<List<PostWithAuthorRelation>>

    @Transaction
    @Query("""
        SELECT posts.* FROM posts 
        INNER JOIN users ON posts.author_id = users.id
        WHERE users.is_followed_by_current_user = 1
        ORDER BY posts.created_at DESC
    """)
    fun getFollowingPostsFlow(): Flow<List<PostWithAuthorRelation>>

    @Transaction
    @Query("""
        SELECT * FROM posts 
        WHERE is_saved_by_user = 1
        ORDER BY created_at DESC
    """)
    fun getSavedPostsFlow(): Flow<List<PostWithAuthorRelation>>

    @Transaction
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostWithAuthorRelation?

    @Transaction
    @Query("SELECT * FROM posts WHERE author_id = :userId ORDER BY created_at DESC")
    fun getPostsByAuthorFlow(userId: String): Flow<List<PostWithAuthorRelation>>

    @Transaction
    @Query("SELECT * FROM posts WHERE id IN (:postIds)")
    fun getPostsWithAuthorByIds(postIds: List<String>): List<PostWithAuthorRelation>

    @Upsert
    suspend fun upsertPosts(posts: List<PostEntity>)

    @Query("""
        UPDATE posts
        SET current_user_vote = :voteValue, total_aura = :newTotalAura
        WHERE id = :postId
    """)
    suspend fun updateLocalVote(postId: String, voteValue: Int, newTotalAura: Int)

    @Query("""
        UPDATE posts
        SET is_saved_by_user = :isSaved, saves_count = :savesCount 
        WHERE id = :postId
    """)
    suspend fun updateLocalSaveState(postId: String, isSaved: Boolean, savesCount: Int)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}