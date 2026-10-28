package com.flexaura.data.repository.comment

import com.flexaura.data.fakes.TestData
import com.flexaura.data.fakes.TestState
import com.flexaura.data.fakes.dao.FakeCommentDao
import com.flexaura.data.fakes.dao.FakePostDao
import com.flexaura.data.fakes.datasource.FakeCommentRemoteDataSource
import com.flexaura.data.mapper.toEntity
import com.flexaura.data.repository.CommentRepositoryImpl
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
class CommentRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private lateinit var fakeCommentDao: FakeCommentDao
    private lateinit var fakePostDao: FakePostDao
    private lateinit var fakeCommentRemoteDataSource: FakeCommentRemoteDataSource
    private lateinit var repository: CommentRepositoryImpl

    @Before
    fun setup() {
        fakeCommentDao = FakeCommentDao()
        fakePostDao = FakePostDao()
        fakeCommentRemoteDataSource = FakeCommentRemoteDataSource(state = TestState())
        repository = CommentRepositoryImpl(
            commentDao = fakeCommentDao,
            postDao = fakePostDao,
            dataSource = fakeCommentRemoteDataSource,
            dispatcher = mainDispatcherRule.testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun `syncComments successful fetches remote and save to local with author data`() = runTest {
        // Given
        val postId = TestData.defaultPost.id
        val author = TestData.defaultUser
        val mockCommentDto = TestData.defaultComment

        fakeCommentRemoteDataSource.state.comments.update { it + mockCommentDto }
        fakeCommentRemoteDataSource.state.users.update { it + author }

        // When
        val result = repository.syncComments(postId = postId)

        // Then
        val localComments = fakeCommentDao.getCommentsByPostId(postId).first()
        val savedComment = localComments.first()

        assertTrue(result.isSuccess)
        assertEquals(1, localComments.size)
        assertEquals(mockCommentDto.id, savedComment.id)
        assertEquals(mockCommentDto.postId, savedComment.postId)
        assertEquals(author.username, savedComment.authorUsername)
        assertEquals(author.totalAura, savedComment.authorTotalAura)
    }

    @Test
    fun `deleteComment removes from local cache and remote data source`() = runTest {
        // Given
        val postId = TestData.defaultPost.id
        val author = TestData.defaultUser
        val commentRemote = TestData.defaultComment
        val commentLocal = commentRemote.toEntity(author = author)

        fakeCommentDao.insertComment(commentLocal)
        fakeCommentRemoteDataSource.state.comments.update { it + commentRemote }

        // When
        val result = repository.deleteComment(commentRemote.id)

        //Then
        val localComments = fakeCommentDao.getCommentsByPostId(postId).first()

        assertTrue(result.isSuccess)
        assertTrue(localComments.isEmpty())
        assertTrue(fakeCommentRemoteDataSource.state.comments.value.isEmpty())
    }
}