package com.flexaura.data.repository.comment

import com.flexaura.core.database.model.CommentEntity
import com.flexaura.core.network.model.CommentDto
import com.flexaura.core.network.model.UserDto
import com.flexaura.data.fakes.FakeCommentDao
import com.flexaura.data.fakes.FakeSupabaseRemoteDataSource
import com.flexaura.data.repository.CommentRepositoryImpl
import com.flexaura.data.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommentRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private lateinit var fakeCommentDao: FakeCommentDao
    private lateinit var fakeSupabaseRemoteDataSource: FakeSupabaseRemoteDataSource
    private lateinit var repository: CommentRepositoryImpl

    @Before
    fun setup() {
        fakeCommentDao = FakeCommentDao()
        fakeSupabaseRemoteDataSource = FakeSupabaseRemoteDataSource()
        repository = CommentRepositoryImpl(
            commentDao = fakeCommentDao,
            networkDataSource = fakeSupabaseRemoteDataSource,
            dispatcher = mainDispatcherRule.testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun `syncComments successful fetches remote and save to local with author data`() = runTest {
        // Given
        val postId = "post_123"
        val authorId = "user_123"
        val mockCommentDto = CommentDto(
            id = "comment_123",
            postId = postId,
            authorId = authorId,
            content = "Test comment",
            createdAt = "2026-01-01T00:00:00Z"
        )
        val mockUserDto = UserDto(
            id = authorId,
            username = "test_user",
            displayName = "Test User",
            imageUrl = "https://example.com/avatar.jpg",
            bio = null,
            createdAt = "2026-01-01T00:00:00Z",
            totalAura = 500
        )

        fakeSupabaseRemoteDataSource.remoteComments.add(mockCommentDto)
        fakeSupabaseRemoteDataSource.remoteUsers[authorId] = mockUserDto

        // When
        val result = repository.syncComments(postId = postId)

        // Then
        val localComments = fakeCommentDao.getCommentsByPostId(postId).first()
        val savedComment = localComments.first()

        assertTrue(result.isSuccess)
        assertEquals(1, localComments.size)
        assertEquals("comment_123", savedComment.id)
        assertEquals("Test comment", savedComment.content)
        assertEquals("test_user", savedComment.authorUsername)
        assertEquals(500, savedComment.authorTotalAura)
    }

    @Test
    fun `deleteComment removes from local cache and remote data source`() = runTest {
        // Given
        val postId = "post_123"
        val commentId = "comment_123"
        val commentRemote = CommentDto(
            id = commentId,
            postId = postId,
            authorId = "author_123",
            content = "delete comment",
            createdAt = "2026-01-01T00:00:00Z"
        )
        val commentLocal = CommentEntity(
            id = commentId,
            postId = postId,
            userId = "author_123",
            content = "delete comment",
            createdAt = "2026-01-01T00:00:00Z",
            authorUsername = "test_user",
            authorDisplayName = "Test User",
            authorAvatarUrl = "https://example.com/avatar.jpg",
            authorTotalAura = 500
        )

        fakeCommentDao.insertComment(commentLocal)
        fakeSupabaseRemoteDataSource.remoteComments.add(commentRemote)

        // When
        val result = repository.deleteComment(commentId)

        //Then
        val localComments = fakeCommentDao.getCommentsByPostId(postId).first()

        assertTrue(result.isSuccess)
        assertTrue(localComments.isEmpty())
        assertTrue(fakeSupabaseRemoteDataSource.remoteComments.isEmpty())
    }
}