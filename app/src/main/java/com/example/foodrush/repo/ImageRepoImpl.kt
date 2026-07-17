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
    // Initializing Cloudinary with exact credentials
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dis3fco3j",
            "api_key" to "531963263852246",
            "api_secret" to "7RGsBio_XrtoGVYzURm-iXlux9A"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "resource_type", "image"
                    )
                )

                val imageUrl = response["secure_url"]?.toString() ?: response["url"]?.toString()

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