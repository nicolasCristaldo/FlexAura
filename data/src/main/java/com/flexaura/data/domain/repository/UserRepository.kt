package com.flexaura.data.domain.repository

import com.flexaura.data.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserFlow(userId: String): Flow<User?>
    suspend fun syncUserProfile(userId: String): Result<Unit>
    suspend fun toggleFollowUser(targetUserId: String): Result<Unit>
    suspend fun updateProfile(
        username: String,
        displayName: String,
        imageBytes: ByteArray? = null,
        bio: String?,
        isImageRemoved: Boolean
    ): Result<Unit>
    suspend fun searchUsers(query: String, page: Int, pageSize: Int): Result<List<User>>
}