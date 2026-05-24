package com.flexaura.data.repository

import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.network.datasource.SupabaseRemoteDataSource
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.di.ApplicationScope
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.model.User
import com.flexaura.data.domain.repository.UserRepository
import com.flexaura.data.mapper.toDomain
import com.flexaura.data.mapper.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val networkDataSource: SupabaseRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
): UserRepository {
    override fun getUserFlow(userId: String): Flow<User?> =
        userDao.getUserIdFlow(userId)
            .map { userEntity -> userEntity.toDomain() }
            .flowOn(dispatcher)

    override suspend fun syncUserProfile(
        currentUserId: String,
        userId: String
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val remoteUser = networkDataSource.getUserProfile(
                userId,
                currentUserId
            ) ?: throw Exception("No user found")
            val userEntity = remoteUser.toEntity()

            userDao.upsertUser(userEntity)
        }
    }

    override suspend fun toggleFollowUser(
        currentUserId: String,
        targetUserId: String
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            val targetUserLocal = userDao.getUserById(targetUserId)
                ?: throw Exception("No user found")
            val wasFollowing = targetUserLocal.isFollowedByCurrentUser
            val newFollowState = !wasFollowing
            val countChange = if (newFollowState) 1 else -1

            userDao.updateFollowState(targetUserId, newFollowState, countChange)

            if (newFollowState) networkDataSource.followUser(currentUserId, targetUserId)
            else networkDataSource.unfollowUser(currentUserId, targetUserId)
        }
    }.await()

    override suspend fun updateProfile(
        userId: String,
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?,
        createdAt: String
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            val updatedUserDto = UserDto(
                id = userId,
                username = username,
                displayName = displayName,
                imageUrl = imageUrl,
                bio = bio,
                createdAt = createdAt
            )
            networkDataSource.updateUser(updatedUserDto)

            val currentUserEntity = userDao.getUserById(userId)
            val isFollowed = currentUserEntity?.isFollowedByCurrentUser ?: false

            userDao.upsertUser(updatedUserDto.toEntity(isFollowed))
        }
    }.await()
}