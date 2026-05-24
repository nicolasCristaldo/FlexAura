package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow(userId: String): Flow<User?>
    suspend fun syncUserProfile(userId: String): Result<Unit>
    suspend fun toggleFollowUser(targetUserId: String): Result<Unit>
    suspend fun updateProfile(
        userId: String,
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?,
        createdAt: String
    ): Result<Unit>
}