package com.flexaura.data.repository

import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.network.datasource.database.SupabaseRemoteDataSource
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
            .map { userEntity -> userEntity?.toDomain() }
            .flowOn(dispatcher)

    override suspend fun syncUserProfile(userId: String): Result<Unit> =
        withContext(dispatcher) {
            runCatching {
                val remoteUser = networkDataSource.getUserProfile(userId)
                    ?: throw Exception("No user found")
                val userEntity = remoteUser.toEntity()

                userDao.upsertUser(userEntity)
            }
        }

    override suspend fun toggleFollowUser(targetUserId: String): Result<Unit> =
        scope.async(dispatcher) {
            runCatching {
                val targetUserLocal = userDao.getUserById(targetUserId)
                    ?: throw Exception("No user found")
                val wasFollowing = targetUserLocal.isFollowedByCurrentUser
                val newFollowState = !wasFollowing
                val countChange = if (newFollowState) 1 else -1

                userDao.updateFollowState(targetUserId, newFollowState, countChange)

                if (newFollowState) networkDataSource.followUser(targetUserId)
                else networkDataSource.unfollowUser(targetUserId)
            }
        }.await()

    override suspend fun updateProfile(
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            val currentId = networkDataSource.currentUserId ?: throw Exception("No user logged in")
            val updatedUserDto = networkDataSource.updateProfile(
                username = username,
                displayName = displayName,
                imageUrl = imageUrl,
                bio = bio
            )
            val currentUserEntity = userDao.getUserById(currentId)
            val isFollowed = currentUserEntity?.isFollowedByCurrentUser ?: false

            userDao.upsertUser(updatedUserDto.toEntity(isFollowed))
        }
    }.await()
}