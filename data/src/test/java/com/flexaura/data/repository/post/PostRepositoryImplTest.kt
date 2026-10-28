package com.flexaura.data.repository.post

import com.flexaura.core.network.model.SaveDto
import com.flexaura.data.fakes.TestData
import com.flexaura.data.fakes.TestState
import com.flexaura.data.fakes.dao.FakePostDao
import com.flexaura.data.fakes.dao.FakeUserDao
import com.flexaura.data.fakes.datasource.FakePostRemoteDataSource
import com.flexaura.data.fakes.datasource.FakeStorageRemoteDataSource
import com.flexaura.data.mapper.toEntity
import com.flexaura.data.repository.MediaRepositoryImpl
import com.flexaura.data.repository.PostRepositoryImpl
import com.flexaura.data.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private lateinit var fakePostDao: FakePostDao
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakePostRemoteDataSource: FakePostRemoteDataSource
    private lateinit var postRepositoryImpl: PostRepositoryImpl
    private lateinit var mediaRepositoryImpl: MediaRepositoryImpl

    @Before
    fun setup() {
        fakeUserDao = FakeUserDao()
        fakePostDao = FakePostDao(usersTable = fakeUserDao.usersTable)
        mediaRepositoryImpl = MediaRepositoryImpl(
            dataSource = FakeStorageRemoteDataSource(),
            dispatcher = mainDispatcherRule.testDispatcher
        )
        fakePostRemoteDataSource = FakePostRemoteDataSource(state = TestState())
        postRepositoryImpl = PostRepositoryImpl(
            postDao = fakePostDao,
            userDao = fakeUserDao,
            dataSource = fakePostRemoteDataSource,
            dispatcher = mainDispatcherRule.testDispatcher,
            mediaRepository = mediaRepositoryImpl,
            scope = testScope
        )
    }

    @Test
    fun `syncPosts successful fetches And Upserts To Local Cache`() = runTest {
        // Given
        val page = 1
        val pageSize = 10
        val remotePost = TestData.defaultPost

        fakePostRemoteDataSource.state.posts.update { it + remotePost }

        // When
        val result = postRepositoryImpl.syncPosts(page, pageSize)

        // Then
        val localPosts = fakePostDao.getPostsFlow().first()

        assertTrue(result.isSuccess)
        assertEquals(1, localPosts.size)
        assertEquals(remotePost.id, localPosts.first().post.id)
        assertEquals(remotePost.content, localPosts.first().post.content)
    }

    @Test
    fun `toggleSavePost, currently unsaved, updates local post to saved and calls remote save`() = runTest {
        // Given
        val currentUser = TestData.defaultUser
        val remotePost = TestData.defaultPost
        val localPost = remotePost.toEntity()

        fakePostRemoteDataSource.state.users.update { it + currentUser }
        fakePostRemoteDataSource.state.posts.update { it + remotePost }
        fakePostDao.upsertPosts(listOf(localPost))
        fakeUserDao.upsertUsers(listOf(currentUser.toEntity()))

        // When
        val result = postRepositoryImpl.toggleSavePost(remotePost.id)

        // Then
        val updatedLocalPost = fakePostDao.getPostById(remotePost.id)

        assertTrue(result.isSuccess)
        assertTrue(updatedLocalPost?.post?.isSavedByUser == true)
        assertEquals(1,fakePostRemoteDataSource.state.saves.value.size)
        assertEquals(remotePost.id, fakePostRemoteDataSource.state.saves.value.first().postId)
        assertTrue(fakePostRemoteDataSource.state.posts.value.first().savesCount == 1)
        assertEquals(fakePostRemoteDataSource.state.saves.value.first().userId, currentUser.id)
    }

    @Test
    fun `toggleSavePost, currently saved, updates local post to unsaved and calls remote unsave`() = runTest {
        // Given
        val currentUser = TestData.defaultUser
        val remotePost = TestData.defaultPost.copy(savesCount = 1)
        val localPost = remotePost.toEntity().copy(isSavedByUser = true)

        fakePostRemoteDataSource.state.users.update { it + currentUser }
        fakePostRemoteDataSource.state.posts.update { it + remotePost }
        fakePostRemoteDataSource.state.saves.update {
            it + SaveDto(
                id = "save_1",
                userId = currentUser.id,
                postId = remotePost.id
            )
        }
        fakePostDao.upsertPosts(listOf(localPost))
        fakeUserDao.upsertUsers(listOf(currentUser.toEntity()))

        // When
        val result = postRepositoryImpl.toggleSavePost(remotePost.id)

        // Then
        val updatedLocalPost = fakePostDao.getPostById(remotePost.id)

        assertTrue(result.isSuccess)
        assertTrue(updatedLocalPost?.post?.isSavedByUser == false)
        assertEquals(0,fakePostRemoteDataSource.state.saves.value.size)
        assertTrue(fakePostRemoteDataSource.state.saves.value.isEmpty())
        assertEquals(0, fakePostRemoteDataSource.state.posts.value.first().savesCount)
    }
}