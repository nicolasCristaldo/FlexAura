package com.flexaura.data.repository.post

import com.flexaura.core.database.model.PostEntity
import com.flexaura.core.network.model.PostDto
import com.flexaura.core.network.model.PostWithInteractionsDto
import com.flexaura.core.network.model.UserSaveIdDto
import com.flexaura.data.fakes.FakePostDao
import com.flexaura.data.fakes.FakeSupabaseRemoteDataSource
import com.flexaura.data.repository.PostRepositoryImpl
import com.flexaura.data.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    private lateinit var fakeSupabaseRemoteDataSource: FakeSupabaseRemoteDataSource
    private lateinit var postRepositoryImpl: PostRepositoryImpl

    @Before
    fun setup() {
        fakePostDao = FakePostDao()
        fakeSupabaseRemoteDataSource = FakeSupabaseRemoteDataSource()
        postRepositoryImpl = PostRepositoryImpl(
            postDao = fakePostDao,
            networkDataSource = fakeSupabaseRemoteDataSource,
            dispatcher = mainDispatcherRule.testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun `syncPosts successful fetches And Upserts To Local Cache`() = runTest {
        // Given
        val page = 1
        val pageSize = 10
        val mockRemotePost = PostWithInteractionsDto(
            post = PostDto(
                id = "post_123",
                authorId = "author_123",
                content = "Test post",
                imageUrl = "https://example.com/image.jpg",
                createdAt = "2026-01-01T00:00:00Z"
            )
        )

        fakeSupabaseRemoteDataSource.remotePosts.add(mockRemotePost)

        // When
        val result = postRepositoryImpl.syncPosts(page, pageSize)

        // Then
        val localPosts = fakePostDao.getPostsFlow().first()

        assertTrue(result.isSuccess)
        assertEquals(1, localPosts.size)
        assertEquals("post_123", localPosts.first().id)
        assertEquals("Test post", localPosts.first().content)
    }

    @Test
    fun `toggleSavePost, currently unsaved, updates local post to saved and calls remote save`() = runTest {
        // Given
        val currentUserId = fakeSupabaseRemoteDataSource.currentUserId!!
        val postId = "post_123"
        val remotePost = PostWithInteractionsDto(
            post = PostDto(
                id = "post_123",
                authorId = "author_123",
                content = "Test post",
                imageUrl = "https://example.com/image.jpg",
                createdAt = "2026-01-01T00:00:00Z"
            )
        )
        val localPost = PostEntity(
            id = postId,
            authorId = "author_123",
            content = "Test post",
            imageUrl = "https://example.com/image.jpg",
            createdAt = "2026-01-01T00:00:00Z",
            commentsCount = 0,
            savesCount = 0,
            currentUserVote = 0,
            totalAura = 0,
            isSavedByUser = false
        )
        fakeSupabaseRemoteDataSource.remotePosts.add(remotePost)
        fakePostDao.upsertPosts(listOf(localPost))

        // When
        val result = postRepositoryImpl.toggleSavePost(currentUserId, postId)

        // Then
        val updatedLocalPost = fakePostDao.getPostById(postId)

        assertTrue(result.isSuccess)
        assertTrue(updatedLocalPost?.isSavedByUser == true)
        assertEquals(1,fakeSupabaseRemoteDataSource.remoteSaves.size)
        assertEquals(postId, fakeSupabaseRemoteDataSource.remoteSaves.first().postId)
        assertTrue(fakeSupabaseRemoteDataSource.remotePosts.first().isSaved)
        assertEquals(fakeSupabaseRemoteDataSource.remotePosts.first().userSave.first().id, currentUserId)
    }

    @Test
    fun `toggleSavePost, currently saved, updates local post to unsaved and calls remote unsave`() = runTest {
        // Given
        val currentUserId = fakeSupabaseRemoteDataSource.currentUserId!!
        val postId = "post_123"
        val remotePost = PostWithInteractionsDto(
            post = PostDto(
                id = "post_123",
                authorId = "author_123",
                content = "Test post",
                imageUrl = "https://example.com/image.jpg",
                createdAt = "2026-01-01T00:00:00Z"
            ),
            userSave = listOf(UserSaveIdDto(id = currentUserId))
        )
        val localPost = PostEntity(
            id = postId,
            authorId = "author_123",
            content = "Test post",
            imageUrl = "https://example.com/image.jpg",
            createdAt = "2026-01-01T00:00:00Z",
            commentsCount = 0,
            savesCount = 0,
            currentUserVote = 0,
            totalAura = 0,
            isSavedByUser = true
        )
        fakeSupabaseRemoteDataSource.remotePosts.add(remotePost)
        fakePostDao.upsertPosts(listOf(localPost))

        // When
        val result = postRepositoryImpl.toggleSavePost(currentUserId, postId)

        // Then
        val updatedLocalPost = fakePostDao.getPostById(postId)

        assertTrue(result.isSuccess)
        assertTrue(updatedLocalPost?.isSavedByUser == false)
        assertEquals(0,fakeSupabaseRemoteDataSource.remoteSaves.size)
        assertTrue(fakeSupabaseRemoteDataSource.remoteSaves.isEmpty())
        assertFalse(fakeSupabaseRemoteDataSource.remotePosts.first().isSaved)
        assertTrue(fakeSupabaseRemoteDataSource.remotePosts.first().userSave.isEmpty())
    }
}