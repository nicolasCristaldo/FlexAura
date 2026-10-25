package com.flexaura.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flexaura.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing local [UserEntity] profile data and relationships in the Room database.
 *
 * Provides reactive streams, suspending lookup queries, upsert operations, and atomic state updates
 * such as follow status and follower count adjustments.
 */
@Dao
interface UserDao {
    /**
     * Retrieves a reactive stream of a user profile by their unique identifier.
     *
     * @param userId The unique identifier of the user to observe.
     * @return A [Flow] emitting the [UserEntity] or `null` if the user is not found locally.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserIdFlow(userId: String): Flow<UserEntity?>

    /**
     * Fetches a single user profile by their unique identifier.
     *
     * @param userId The unique identifier of the user.
     * @return The corresponding [UserEntity], or `null` if it does not exist locally.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    /**
     * Inserts or updates a single user entity in the local database.
     *
     * @param user The [UserEntity] to upsert.
     */
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    /**
     * Inserts or updates a list of user entities in the local database in bulk.
     *
     * @param users The list of [UserEntity] items to upsert.
     */
    @Upsert
    suspend fun upsertUsers(users: List<UserEntity>)

    /**
     * Updates the follow status of a specific user and adjusts their follower count locally.
     *
     * @param userId The unique identifier of the user whose follow state is being updated.
     * @param isFollowing Boolean flag indicating whether the current user is following this profile.
     * @param countChange The integer delta to apply to the follower count (e.g., `1` when following, `-1` when unfollowing).
     */
    @Query("""
        UPDATE users
        SET is_followed_by_current_user = :isFollowing,
            followers_count = MAX(0, followers_count + :countChange)
        WHERE id = :userId
    """)
    suspend fun updateFollowState(userId: String, isFollowing: Boolean, countChange: Int)

    /**
     * Deletes a specific user from the local database by their ID.
     *
     * @param userId The unique identifier of the user to delete.
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    /**
     * Deletes all cached user profiles from the local database table.
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}