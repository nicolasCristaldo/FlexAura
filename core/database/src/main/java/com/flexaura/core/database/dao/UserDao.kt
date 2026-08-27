package com.flexaura.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flexaura.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Upsert
    suspend fun upsertUsers(users: List<UserEntity>)

    @Query("""
        UPDATE users
        SET is_followed_by_current_user = :isFollowing,
            followers_count = MAX(0, followers_count + :countChange)
        WHERE id = :userId
    """)
    suspend fun updateFollowState(userId: String, isFollowing: Boolean, countChange: Int)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}