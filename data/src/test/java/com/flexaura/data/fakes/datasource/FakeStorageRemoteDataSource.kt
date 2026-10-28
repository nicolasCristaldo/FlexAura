package com.flexaura.data.fakes.datasource

import com.flexaura.core.network.datasource.database.storage.StorageRemoteDataSource

class FakeStorageRemoteDataSource: StorageRemoteDataSource {
    val storage = mutableMapOf<String, ByteArray>()
    val fakeUrl = "https://flexaura-storage.fake"

    override suspend fun uploadImage(
        bucketName: String,
        path: String,
        imageBytes: ByteArray,
        upsert: Boolean
    ): String {
        val storageKey = "$bucketName/$path"

        storage[storageKey] = imageBytes

        return "$fakeUrl/$storageKey"
    }

    override suspend fun deleteImage(bucketName: String, path: String) {
        val storageKey = "$bucketName/$path"

        storage.remove(storageKey)
    }
}