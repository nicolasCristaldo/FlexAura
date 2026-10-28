package com.flexaura.data.repository.user

import com.flexaura.core.database.model.UserEntity
import com.flexaura.core.network.datasource.auth.UserSession
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.fakes.TestData
import com.flexaura.data.fakes.TestState
import com.flexaura.data.fakes.dao.FakeUserDao
import com.flexaura.data.fakes.datasource.FakeStorageRemoteDataSource
import com.flexaura.data.fakes.datasource.FakeUserRemoteDataSource
import com.flexaura.data.repository.MediaRepositoryImpl
import com.flexaura.data.repository.UserRepositoryImpl
import com.flexaura.data.rule.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
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
    private lateinit var fakeUserRemoteDataSource: FakeUserRemoteDataSource
    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var mediaRepositoryImpl: MediaRepositoryImpl
    private lateinit var userSession: UserSession

    @Before
    fun setup() {
        userSession = mockk(relaxed = true)
        coEvery { userSession.requireCurrentUserId() } returns TestData.defaultUser.id
        mediaRepositoryImpl = MediaRepositoryImpl(
            dataSource = FakeStorageRemoteDataSource(),
            dispatcher = mainDispatcherRule.testDispatcher
        )
        fakeUserDao = FakeUserDao()
        fakeUserRemoteDataSource = FakeUserRemoteDataSource(state = TestState())
        userRepositoryImpl = UserRepositoryImpl(
            userDao = fakeUserDao,
            dataSource = fakeUserRemoteDataSource,
            mediaRepository = mediaRepositoryImpl,
            dispatcher = mainDispatcherRule.testDispatcher,
            userSession = userSession,
            scope = testScope
        )
    }

    @Test
    fun `syncUserProfile successful fetches remote And Upserts To Local Cache`() = runTest {
        // Given
        val remoteUser = TestData.defaultUser

        fakeUserRemoteDataSource.state.users.update { it + remoteUser }

        // When
        val result = userRepositoryImpl.syncUserProfile(remoteUser.id)

        // Then
        val localUser = fakeUserDao.getUserById(remoteUser.id)

        assertTrue(result.isSuccess)
        assertEquals(remoteUser.username, localUser?.username)
        assertEquals(remoteUser.displayName, localUser?.displayName)
        assertEquals(remoteUser.totalAura, localUser?.totalAura)
    }

    @Test
    fun `toggleFollowUser, currently not following, follows and calls remote follow`() = runTest {
        // Given
        val currentUser = TestData.defaultUser
        val targetUserId = "user_target"
        val targetUserDto = UserDto(
            id = targetUserId,
            username = "target_user",
            displayName = "Target User",
            createdAt = "2024-01-01T00:00:00Z"
        )
        val targetLocalUser = UserEntity(
            id = targetUserId,
            username = "target_user",
            displayName = "Target User",
            imageUrl = null,
            bio = null,
            createdAt = "2024-01-01T00:00:00Z",
            totalAura = 0,
            followersCount = 0,
            followingCount = 0,
            isFollowedByCurrentUser = false
        )

        fakeUserRemoteDataSource.state.users.update { listOf(currentUser, targetUserDto) }
        fakeUserDao.upsertUser(targetLocalUser)

        // When
        val result = userRepositoryImpl.toggleFollowUser(targetUserId)

        // Then
        val updatedLocalUser = fakeUserDao.getUserById(targetUserId)

        assertTrue(result.isSuccess)
        assertEquals(true, updatedLocalUser?.isFollowedByCurrentUser)
        assertEquals(1, updatedLocalUser?.followersCount)
    }

    @Test
    fun `toggleFollowUser, currently following, unfollow and calls remote unfollow`() = runTest {
        // Given
        val currentUser = TestData.defaultUser
        val targetUserId = "user_target"
        val targetUserDto = UserDto(
            id = targetUserId,
            username = "target_user",
            displayName = "Target User",
            createdAt = "2024-01-01T00:00:00Z"
        )
        val targetLocalUser = UserEntity(
            id = targetUserId,
            username = "target_user",
            displayName = "Target User",
            imageUrl = null,
            bio = null,
            createdAt = "2024-01-01T00:00:00Z",
            totalAura = 0,
            followersCount = 1,
            followingCount = 0,
            isFollowedByCurrentUser = true
        )

        fakeUserRemoteDataSource.state.users.update { listOf(currentUser, targetUserDto) }
        fakeUserDao.upsertUser(targetLocalUser)

        // When
        val result = userRepositoryImpl.toggleFollowUser(targetUserId)

        // Then
        val updatedLocalUser = fakeUserDao.getUserById(targetUserId)

        assertTrue(result.isSuccess)
        assertEquals(false, updatedLocalUser?.isFollowedByCurrentUser)
        assertEquals(0, updatedLocalUser?.followersCount)
    }
}