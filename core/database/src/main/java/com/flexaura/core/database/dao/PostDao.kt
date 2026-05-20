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

    @Upsert
    suspend fun upsertPosts(posts: List<PostEntity>)

    @Query("UPDATE posts SET current_user_vote = :voteValue WHERE id = :postId")
    suspend fun updateLocalVote(postId: String, voteValue: Int)

    @Query("UPDATE posts SET is_saved_by_user = :isSaved WHERE id = :postId")
    suspend fun updateLocalSaveState(postId: String, isSaved: Boolean)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostByid(postId: String)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}