package com.flexaura.core.network.datasource.database.storage

interface StorageRemoteDataSource {
    suspend fun uploadImage(bucketName: String, path: String, imageBytes: ByteArray, upsert: Boolean = false): String
    suspend fun deleteImage(bucketName: String, path: String)
}