package com.flexaura.data.domain.repository

interface MediaRepository {
    suspend fun uploadPostImage(postId: String, imageBytes: ByteArray): Result<String>
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): Result<String>
    suspend fun deleteImage(bucketName: String, fileName: String): Result<Unit>
}