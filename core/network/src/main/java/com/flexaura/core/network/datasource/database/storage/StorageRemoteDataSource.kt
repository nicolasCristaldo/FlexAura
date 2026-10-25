package com.flexaura.core.network.datasource.database.storage

/**
 * Interface defining remote file storage operations using Supabase Storage.
 *
 * Provides suspending methods to upload and delete files (such as images) within storage buckets.
 */
interface StorageRemoteDataSource {
    /**
     * Uploads an image byte array to a specified bucket and path, returning its public URL.
     *
     * @param bucketName The name of the storage bucket.
     * @param path The file path or name within the bucket.
     * @param imageBytes The raw byte array of the image to upload.
     * @param upsert Whether to overwrite an existing file at the given path. Defaults to `false`.
     * @return The public URL of the uploaded image.
     */
    suspend fun uploadImage(bucketName: String, path: String, imageBytes: ByteArray, upsert: Boolean = false): String

    /**
     * Deletes a file from a specified storage bucket and path.
     *
     * @param bucketName The name of the storage bucket.
     * @param path The file path or name within the bucket to delete.
     */
    suspend fun deleteImage(bucketName: String, path: String)
}