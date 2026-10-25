package com.flexaura.data.domain.repository

/**
 * Repository interface managing media file uploads and deletions via cloud storage.
 *
 * Provides suspending methods for uploading post images and user avatars, and deleting files from storage buckets.
 */
interface MediaRepository {
    /**
     * Uploads an image for a specific post to remote storage.
     *
     * @param postId The unique identifier of the post.
     * @param imageBytes The raw byte array of the image.
     * @return A [Result] containing the public URL of the uploaded image upon success, or an exception on failure.
     */
    suspend fun uploadPostImage(postId: String, imageBytes: ByteArray): Result<String>

    /**
     * Uploads or overwrites an avatar image for a specific user profile.
     *
     * @param userId The unique identifier of the user.
     * @param imageBytes The raw byte array of the avatar image.
     * @return A [Result] containing the public URL of the uploaded avatar upon success, or an exception on failure.
     */
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): Result<String>

    /**
     * Deletes a file from a specified storage bucket.
     *
     * @param bucketName The name of the storage bucket.
     * @param fileName The name or path of the file to delete.
     * @return A [Result] indicating success or containing an exception if deletion fails.
     */
    suspend fun deleteImage(bucketName: String, fileName: String): Result<Unit>
}