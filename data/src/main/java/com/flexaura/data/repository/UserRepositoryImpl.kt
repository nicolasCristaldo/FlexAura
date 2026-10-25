package com.flexaura.data.repository

import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.network.datasource.auth.UserSession
import com.flexaura.core.network.datasource.database.user.UserRemoteDataSource
import com.flexaura.data.di.ApplicationScope
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.model.User
import com.flexaura.data.domain.repository.MediaRepository
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

/**
 * Implementation of [UserRepository] utilizing local Room DAOs, remote data sources,
 * user session management, and media repositories to handle user profiles and social workflows.
 *
 * @property userDao Data Access Object for local user caching and follower count updates.
 * @property userSession Helper class to obtain the currently authenticated user's ID.
 * @property dataSource Remote data source for user profile queries, updates, searches, and follow actions.
 * @property mediaRepository Repository for handling avatar uploads and deletions.
 * @property dispatcher The coroutine dispatcher for background thread execution.
 * @property scope The coroutine scope used for executing asynchronous tasks.
 */
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userSession: UserSession,
    private val dataSource: UserRemoteDataSource,
    private val mediaRepository: MediaRepository,
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
                val remoteUser = dataSource.getUserProfile(userId)
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

                try {
                    if (newFollowState) dataSource.followUser(targetUserId)
                    else dataSource.unfollowUser(targetUserId)
                }
                catch (e: Exception) {
                    userDao.updateFollowState(targetUserId, wasFollowing, -countChange)
                    throw e
                }
            }
        }.await()

    override suspend fun updateProfile(
        username: String,
        displayName: String,
        imageBytes: ByteArray?,
        bio: String?,
        isImageRemoved: Boolean
    ): Result<Unit> = scope.async(dispatcher) {
        runCatching {
            val currentId = userSession.currentUserId ?: throw Exception("No user logged in")

            val newImage = when {
                imageBytes != null -> {
                    mediaRepository.uploadAvatar(userId = currentId, imageBytes = imageBytes).getOrThrow()
                }
                isImageRemoved -> {
                    runCatching {
                        mediaRepository.deleteImage("avatars", "$currentId.jpg")
                    }
                    null
                }
                else -> {
                    userDao.getUserById(currentId)?.imageUrl
                }
            }

            val updatedUserDto = dataSource.updateProfile(
                username = username,
                displayName = displayName,
                imageUrl = newImage,
                bio = bio
            )
            val currentUserEntity = userDao.getUserById(currentId)
            val isFollowed = currentUserEntity?.isFollowedByCurrentUser ?: false

            userDao.upsertUser(updatedUserDto.toEntity(isFollowed))
        }
    }.await()

    override suspend fun searchUsers(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<List<User>> = withContext(dispatcher) {
        runCatching {
            val remoteUsers = dataSource.searchUsers(query, page, pageSize)

            if(remoteUsers.isEmpty()) return@runCatching emptyList()

            remoteUsers.map { userDto ->
                val localUser = userDao.getUserById(userDto.id)
                val isFollowed = localUser?.isFollowedByCurrentUser ?: false
                val userEntity = userDto.toEntity(isFollowed)

                userDao.upsertUser(userEntity)

                userEntity.toDomain()
            }
        }
    }
}