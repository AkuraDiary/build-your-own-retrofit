package com.asthiseta.diyretrofit.networking.glidealternative

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ImageLoader(private val imageUrl: String, private val imageView: ImageView) {
    fun loadImage() {
        Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000 // Set timeout to 5 seconds
                connection.readTimeout = 5000
                val inputStream = BufferedInputStream(connection.inputStream)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Update UI on the main thread
                imageView.post {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}