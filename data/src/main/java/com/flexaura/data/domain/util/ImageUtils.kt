package com.flexaura.data.domain.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

/**
 * Compresses an image from a given URI and returns it as a JPEG byte array.
 *
 * @receiver The Android [Context] used to open the content resolver stream.
 * @param uri The [Uri] of the target image to compress.
 * @param quality The compression quality percentage (0 to 100). Defaults to 60.
 * @return A [ByteArray] containing the compressed JPEG image data.
 */
fun Context.compressAndGetImageBytes(uri: Uri, quality: Int = 60): ByteArray {
    val inputStream = contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}