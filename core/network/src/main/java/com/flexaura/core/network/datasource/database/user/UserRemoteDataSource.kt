package com.flexaura.core.network.datasource.database.user

import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserProfileDto

interface UserRemoteDataSource {
    // Users
    suspend fun getUser(userId: String): UserDto?
    suspend fun updateProfile(username: String, displayName: String, imageUrl: String?, bio: String?): UserDto
    suspend fun getUserProfile(userId: String): UserProfileDto?
    suspend fun searchUsers(query: String, page: Int, pageSize: Int): List<UserDto>

    // Follows
    suspend fun followUser(followingId: String)
    suspend fun unfollowUser(followingId: String)
    suspend fun getFollowers(userId: String, page: Int, pageSize: Int): List<UserDto>
    suspend fun getFollowing(userId: String, page: Int, pageSize: Int): List<UserDto>
}