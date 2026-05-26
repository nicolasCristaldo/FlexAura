package com.flexaura.data.fakes

import com.flexaura.core.database.dao.UserDao
import com.flexaura.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class FakeUserDao : UserDao {
    val usersTable = MutableStateFlow<List<UserEntity>>(emptyList())

    override fun getUserIdFlow(userId: String): Flow<UserEntity> =
        usersTable.map { list -> list.find { it.id == userId } }.filterNotNull()

    override suspend fun getUserById(userId: String): UserEntity? =
        usersTable.value.find { it.id == userId }

    override suspend fun upsertUser(user: UserEntity) {
        val currentUsers = usersTable.value.toMutableList()
        currentUsers.removeAll { it.id == user.id }
        currentUsers.add(user)
        usersTable.value = currentUsers
    }

    override suspend fun updateFollowState(
        userId: String,
        isFollowing: Boolean,
        countChange: Int
    ) {
        usersTable.value = usersTable.value.map { user ->
            if (user.id == userId) {
                user.copy(
                    isFollowedByCurrentUser = isFollowing,
                    followersCount = user.followersCount + countChange
                )
            }
            else user
        }
    }

    override suspend fun deleteUserById(userId: String) {
        usersTable.value = usersTable.value.filter { it.id != userId }
    }

    override suspend fun deleteAllUsers() {
        usersTable.value = emptyList()
    }
}