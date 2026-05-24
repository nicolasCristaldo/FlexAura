package com.flexaura.data.repository.user

import com.flexaura.core.database.model.UserEntity
import com.flexaura.core.network.model.FollowDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.fakes.FakeSupabaseRemoteDataSource
import com.flexaura.data.fakes.FakeUserDao
import com.flexaura.data.repository.UserRepositoryImpl
import com.flexaura.data.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakeSupabaseRemoteDataSource: FakeSupabaseRemoteDataSource
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Before
    fun setup() {
        fakeUserDao = FakeUserDao()
        fakeSupabaseRemoteDataSource = FakeSupabaseRemoteDataSource()
        userRepositoryImpl = UserRepositoryImpl(
            userDao = fakeUserDao,
            networkDataSource = fakeSupabaseRemoteDataSource,
            dispatcher = mainDispatcherRule.testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun `syncUserProfile successful fetches remote And Upserts To Local Cache`() = runTest {
        // Given
        val currentUserId = "current_user_123"
        val userId = "user_123"
        val remoteUser = UserDto(
            id = userId,
            username = "test_user",
            displayName = "Test User",
            imageUrl = "https://example.com/image.jpg",
            bio = "This is a test user",
            createdAt = "2026-01-01T00:00:00Z",
            totalAura = 90
        )
        fakeSupabaseRemoteDataSource.remoteUsers[userId] = remoteUser

        // When
        val result = userRepositoryImpl.syncUserProfile(currentUserId, remoteUser.id)

        // Then
        val localUser = fakeUserDao.getUserById(userId)

        assertTrue(result.isSuccess)
        assertEquals("test_user", localUser?.username)
        assertEquals("Test User", localUser?.displayName)
        assertEquals(90, localUser?.totalAura)
    }

    @Test
    fun `toggleFollowUser, currently not following, follows and calls remote follow`() = runTest {
        // Given
        val currentUserId = "current_user_123"
        val targetUserId = "user_123"
        val remoteUser = UserDto(
            id = targetUserId,
            username = "test_user",
            displayName = "Test User",
            createdAt = ""
        )
        val targetLocalUser = UserEntity(
            id = targetUserId,
            username = "test_user",
            displayName = "Test User",
            imageUrl = null,
            bio = null,
            createdAt = "",
            totalAura = 0,
            followersCount = 0,
            followingCount = 0,
            isFollowedByCurrentUser = false
        )
        fakeSupabaseRemoteDataSource.remoteUsers[targetUserId] = remoteUser
        fakeSupabaseRemoteDataSource.remoteUsers[currentUserId] = UserDto(
            id = currentUserId,
            username = "current_user",
            displayName = "current_user",
            createdAt = ""
        )
        fakeUserDao.usersTable.value = listOf(targetLocalUser)

        // When
        val result = userRepositoryImpl.toggleFollowUser(currentUserId, targetUserId)

        // Then
        val updatedLocalUser = fakeUserDao.getUserById(targetUserId)

        assertTrue(result.isSuccess)
        assertEquals(true, updatedLocalUser?.isFollowedByCurrentUser)
        assertEquals(1, updatedLocalUser?.followersCount)
        assertTrue(fakeSupabaseRemoteDataSource.remoteFollows.isNotEmpty())
    }

    @Test
    fun `toggleFollowUser, currently following, unfollow and calls remote unfollow`() = runTest {
        // Given
        val currentUserId = "current_user_123"
        val targetUserId = "user_123"
        val remoteUser = UserDto(
            id = targetUserId,
            username = "test_user",
            displayName = "Test User",
            createdAt = ""
        )
        val targetLocalUser = UserEntity(
            id = targetUserId,
            username = "test_user",
            displayName = "Test User",
            imageUrl = null,
            bio = null,
            createdAt = "",
            totalAura = 0,
            followersCount = 10,
            followingCount = 0,
            isFollowedByCurrentUser = true
        )
        fakeSupabaseRemoteDataSource.remoteUsers[targetUserId] = remoteUser
        fakeSupabaseRemoteDataSource.remoteUsers[currentUserId] = UserDto(
            id = currentUserId,
            username = "current_user",
            displayName = "current_user",
            createdAt = ""
        )
        fakeUserDao.usersTable.value = listOf(targetLocalUser)
        fakeSupabaseRemoteDataSource.remoteFollows.add(
            FollowDto(
                followerId = currentUserId,
                followingId = targetUserId
            )
        )

        // When
        val result = userRepositoryImpl.toggleFollowUser(currentUserId, targetUserId)

        // Then
        val updatedLocalUser = fakeUserDao.getUserById(targetUserId)

        assertTrue(result.isSuccess)
        assertEquals(false, updatedLocalUser?.isFollowedByCurrentUser)
        assertEquals(9, updatedLocalUser?.followersCount)
        assertEquals(0, fakeSupabaseRemoteDataSource.remoteFollows.size)
    }
}