package com.flexaura.data.fakes.datasource

import com.flexaura.core.network.datasource.database.user.UserRemoteDataSource
import com.flexaura.core.network.model.UserDto
import com.flexaura.core.network.model.UserFollowCheckDto
import com.flexaura.core.network.model.UserProfileDto
import com.flexaura.data.fakes.TestState
import kotlinx.coroutines.flow.update

class FakeUserRemoteDataSource(
    val state: TestState
): UserRemoteDataSource {

    override suspend fun getUser(userId: String): UserDto? =
        state.users.value.find { it.id == userId }

    override suspend fun updateProfile(
        username: String,
        displayName: String,
        imageUrl: String?,
        bio: String?
    ): UserDto {
        val currentUser = getUser(state.currentUserId) ?: throw Exception("User not found")
        val updatedUser = currentUser.copy(
            username = username,
            displayName = displayName,
            imageUrl = imageUrl,
            bio = bio
        )

        state.users.update { users ->
            users.map { if (it.id == currentUser.id) updatedUser else it }
        }

        return updatedUser
    }

    override suspend fun getUserProfile(userId: String): UserProfileDto? {
        val currentUser = getUser(state.currentUserId) ?: return null
        val user = getUser(userId) ?: return null
        val isFollowing = currentUser.follows.any { it.id == user.id }

        return UserProfileDto(
            id = user.id,
            username = user.username,
            displayName = user.displayName,
            imageUrl = user.imageUrl,
            bio = user.bio,
            totalAura = user.totalAura,
            followersCount = user.followersCount,
            followingCount = user.followingCount,
            createdAt = user.createdAt,
            isFollowingList = if (isFollowing) {
                listOf(UserFollowCheckDto(id = userId))
            }
            else emptyList()
        )
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> =
        state.users.value
            .filter {
                it.username.contains(query, ignoreCase = true) ||
                it.displayName.contains(query, ignoreCase = true)
            }
            .drop((page - 1) * pageSize)
            .take(pageSize)

    override suspend fun followUser(followingId: String) =
        state.users.update { users ->
            users.map { user ->
                when (user.id) {
                    state.currentUserId -> user.copy(followingCount = user.followingCount + 1)
                    followingId -> user.copy(followersCount = user.followersCount + 1)
                    else -> user
                }
            }
        }

    override suspend fun unfollowUser(followingId: String) =
        state.users.update { users ->
            users.map { user ->
                when (user.id) {
                    state.currentUserId -> user.copy(followingCount = user.followingCount - 1)
                    followingId -> user.copy(followersCount = user.followersCount - 1)
                    else -> user
                }
            }
        }

    override suspend fun getFollowers(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        return emptyList()
    }

    override suspend fun getFollowing(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<UserDto> {
        return emptyList()
    }
}