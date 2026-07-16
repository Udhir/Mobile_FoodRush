package com.example.foodrush.repo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.InputStream
import java.util.concurrent.Executors

class ImageRepoImpl : ImageRepo {
    // Initializing Cloudinary with your exact credentials
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dis3fco3j",
            "api_key" to "531963263852246",
            "api_secret" to "7RGsBio_XrtoGVYzURm-iXlux9A"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        // Runs the upload on a background thread so the app doesn't freeze
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)

                // FIXED: We removed the "public_id" parameter.
                // Now, Cloudinary will auto-generate a random, unique ID for every single photo.
                // This completely prevents the caching/deleted image bugs!
                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "resource_type", "image"
                    )
                )

                // FIXED: Get the guaranteed secure HTTPS link directly from Cloudinary
                // Using .toString() ensures we get a String even if the response object is different
                val imageUrl = response["secure_url"]?.toString() ?: response["url"]?.toString()

                // Returns the URL back to the main UI thread
                Handler(Looper.getMainLooper()).post {
                    if (imageUrl != null) {
                        callback(imageUrl)
                    } else {
                        callback(null)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    // Keep this function here because your ImageRepo interface requires it,
    // even though we aren't using it for the upload anymore!
    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}