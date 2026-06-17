package com.flexaura.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flexaura.core.database.model.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getPostsFlow(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT * FROM posts WHERE author_id = :userId ORDER BY created_at DESC")
    fun getPostsByAuthorFlow(userId: String): Flow<List<PostEntity>>

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
    suspend fun deletePostByid(postId: String)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}