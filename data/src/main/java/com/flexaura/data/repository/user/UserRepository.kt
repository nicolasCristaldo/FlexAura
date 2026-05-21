package com.flexaura.data.repository.user

import com.flexaura.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow(userId: String): Flow<User?>
    suspend fun syncUserProfile(currentUserId: String, userId: String): Result<Unit>
    suspend fun toggleFollowUser(currentUserId: String, targetUserId: String): Result<Unit>
    suspend fun updateProfile(
        userId: String,
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?,
        createdAt: String
    ): Result<Unit>
}