package com.flexaura.core.network.datasource.database.user

import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto

/**
 * Interface defining remote user profile and social relationship operations using Supabase.
 *
 * Provides suspending methods for fetching user profiles, updating account details, searching users,
 * managing follow/unfollow actions, and paginated follower/following lists.
 */
interface UserRemoteDataSource {
    // Users
    /**
     * Fetches a user profile DTO by their unique identifier.
     *
     * @param userId The unique identifier of the user to retrieve.
     * @return The corresponding [UserDto], or `null` if the user is not found.
     */
    suspend fun getUser(userId: String): UserDto?

    /**
     * Updates the currently authenticated user's profile information remotely.
     *
     * @param username The updated username handle.
     * @param displayName The updated display name.
     * @param imageUrl The updated profile image URL, if any.
     * @param bio The updated biography text, if any.
     * @return The updated [UserDto] returned by the server.
     */
    suspend fun updateProfile(username: String, displayName: String, imageUrl: String?, bio: String?): UserDto

    /**
     * Fetches detailed profile information for a user, including their follow status relative to the current session.
     *
     * @param userId The unique identifier of the target user profile.
     * @return The corresponding [UserProfileDto], or `null` if the user does not exist.
     */
    suspend fun getUserProfile(userId: String): UserProfileDto?

    /**
     * Searches for user profiles matching a query string with pagination support.
     *
     * @param query The search keyword to match against usernames, display names, or bios.
     * @param page The pagination page index to retrieve.
     * @param pageSize The number of users per page.
     * @return A list of matching [UserDto] items.
     */
    suspend fun searchUsers(query: String, page: Int, pageSize: Int): List<UserDto>

    // Follows
    /**
     * Creates a follow relationship from the current user to a target user.
     *
     * @param followingId The unique identifier of the user to follow.
     */
    suspend fun followUser(followingId: String)

    /**
     * Removes an existing follow relationship from the current user to a target user.
     *
     * @param followingId The unique identifier of the user to unfollow.
     */
    suspend fun unfollowUser(followingId: String)

    /**
     * Retrieves a paginated list of users following a specified user profile.
     *
     * @param userId The unique identifier of the user whose followers are being fetched.
     * @param page The pagination page index.
     * @param pageSize The number of items per page.
     * @return A list of follower [UserDto] objects.
     */
    suspend fun getFollowers(userId: String, page: Int, pageSize: Int): List<UserDto>

    /**
     * Retrieves a paginated list of users followed by a specified user profile.
     *
     * @param userId The unique identifier of the user whose following list is being fetched.
     * @param page The pagination page index.
     * @param pageSize The number of items per page.
     * @return A list of followed [UserDto] objects.
     */
    suspend fun getFollowing(userId: String, page: Int, pageSize: Int): List<UserDto>
}