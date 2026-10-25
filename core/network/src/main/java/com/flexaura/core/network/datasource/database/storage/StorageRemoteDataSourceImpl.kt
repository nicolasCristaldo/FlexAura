package com.flexaura.core.network.datasource.database.storage

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [StorageRemoteDataSource] utilizing the Supabase storage client for file management.
 *
 * @property supabaseClient The Supabase client instance used to access storage buckets.
 */
@Singleton
class StorageRemoteDataSourceImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
): StorageRemoteDataSource {
    override suspend fun uploadImage(
        bucketName: String,
        path: String,
        imageBytes: ByteArray,
        upsert: Boolean
    ): String {
        val bucket = supabaseClient.storage.from(bucketName)

        bucket.upload(path, imageBytes) {
            this.upsert = upsert
        }

        return bucket.publicUrl(path)
    }

    override suspend fun deleteImage(bucketName: String, path: String) {
        supabaseClient.storage.from(bucketName).delete(path)
    }
}