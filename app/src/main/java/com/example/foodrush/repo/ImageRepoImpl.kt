package com.example.foodrush.repo

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ImageRepoImpl : ImageRepo {
    // 1. Get a reference to Firebase Storage
    private val storageRef = FirebaseStorage.getInstance().reference

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        // 2. Create a unique random file name for the image
        val fileName = UUID.randomUUID().toString() + ".jpg"

        // 3. Create a folder in Firebase Storage called "food_images"
        val imageRef = storageRef.child("food_images/$fileName")

        // 4. Upload the file to Firebase
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // 5. If upload succeeds, get the downloadable URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Handler(Looper.getMainLooper()).post {
                        callback(uri.toString())
                    }
                }.addOnFailureListener {
                    Handler(Looper.getMainLooper()).post {
                        callback(null)
                    }
                }
            }
            .addOnFailureListener {
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        // We no longer need to parse the real file name because we use a random UUID above!
        return "image.jpg"
    }
}