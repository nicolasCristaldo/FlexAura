package com.flexaura.data.repository.media

import com.flexaura.data.fakes.datasource.FakeStorageRemoteDataSource
import com.flexaura.data.repository.MediaRepositoryImpl
import com.flexaura.data.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeStorageRemoteDataSource: FakeStorageRemoteDataSource
    private lateinit var mediaRepositoryImpl: MediaRepositoryImpl

    @Before
    fun setup() {
        fakeStorageRemoteDataSource = FakeStorageRemoteDataSource()
        mediaRepositoryImpl = MediaRepositoryImpl(
            dataSource = fakeStorageRemoteDataSource,
            dispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `uploadAvatar successful upload`() = runTest {
        // Given
        val userId = "user123"
        val imageBytes = byteArrayOf(1, 2, 3)

        // When
        val result = mediaRepositoryImpl.uploadAvatar(userId, imageBytes)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, fakeStorageRemoteDataSource.storage.size)
    }

    @Test
    fun `deleteImage successful delete`() = runTest {
        // Given
        val bucketName = "avatars"
        val fileName = "file123.jpg"
        val storageKey = "$bucketName/$fileName"

        fakeStorageRemoteDataSource.storage[storageKey] = byteArrayOf(1, 2, 3)

        // When
        val result = mediaRepositoryImpl.deleteImage(bucketName, fileName)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(fakeStorageRemoteDataSource.storage.isEmpty())
    }
}