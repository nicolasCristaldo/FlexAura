package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface managing user profile data, social follow relationships, profile updates, and user searches.
 *
 * Provides reactive user profile streams, synchronization methods, follow toggling with rollback support,
 * avatar media management, and search capabilities.
 */
interface UserRepository {
    /**
     * Retrieves a reactive stream of a user's profile mapped to a domain model.
     *
     * @param userId The unique identifier of the user.
     * @return A [Flow] emitting the domain [User] or `null` if not found.
     */
    fun getUserFlow(userId: String): Flow<User?>

    /**
     * Fetches user profile details from the remote server and updates the local database cache.
     *
     * @param userId The unique identifier of the user whose profile is being synced.
     * @return A [Result] indicating success or containing an exception if synchronization fails.
     */
    suspend fun syncUserProfile(userId: String): Result<Unit>

    /**
     * Toggles the follow/unfollow state for a target user, updating local follower counts optimistically
     * and syncing remotely with automatic rollback on error.
     *
     * @param targetUserId The unique identifier of the user to follow or unfollow.
     * @return A [Result] indicating success or containing an exception if the action fails.
     */
    suspend fun toggleFollowUser(targetUserId: String): Result<Unit>

    /**
     * Updates the current user's profile information, handling optional avatar uploads or image removals.
     *
     * @param username The updated username handle.
     * @param displayName The updated display name.
     * @param imageBytes The raw byte array of the new avatar image, if any.
     * @param bio The updated biography description, if any.
     * @param isImageRemoved Flag indicating whether the existing avatar image should be deleted.
     * @return A [Result] indicating success or containing an exception if the update fails.
     */
    suspend fun updateProfile(
        username: String,
        displayName: String,
        imageBytes: ByteArray? = null,
        bio: String?,
        isImageRemoved: Boolean
    ): Result<Unit>

    /**
     * Searches for user profiles matching a query string remotely, caches results locally, and returns domain models.
     *
     * @param query The search keyword.
     * @param page The pagination page index.
     * @param pageSize The number of users per page.
     * @return A [Result] containing a list of matching domain [User] items upon success, or an exception on failure.
     */
    suspend fun searchUsers(query: String, page: Int, pageSize: Int): Result<List<User>>
}