package com.flexaura.data.repository

import com.flexaura.core.network.datasource.database.storage.StorageRemoteDataSource
import com.flexaura.data.di.IoDispatcher
import com.flexaura.data.domain.repository.MediaRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val dataSource: StorageRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): MediaRepository {
    override suspend fun uploadPostImage(
        postId: String,
        imageBytes: ByteArray
    ): Result<String> = withContext(dispatcher) {
        runCatching {
            val fileName = "$postId.jpg"
            dataSource.uploadImage(
                bucketName = "posts",
                path = fileName,
                imageBytes = imageBytes,
                upsert = false
            )
        }
    }

    override suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray
    ): Result<String> = withContext(dispatcher) {
        runCatching {
            val fileName = "$userId.jpg"
            val publicUrl = dataSource.uploadImage(
                bucketName = "avatars",
                path = fileName,
                imageBytes = imageBytes,
                upsert = true
            )

            publicUrl
        }
    }

    override suspend fun deleteImage(
        bucketName: String,
        fileName: String
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            dataSource.deleteImage(bucketName, fileName)
        }
    }
}