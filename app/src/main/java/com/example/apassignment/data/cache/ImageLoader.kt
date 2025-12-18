package com.example.apassignment.data.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.coroutines.CoroutineContext
class ImageLoader(
    private val memoryCache: MemoryImageCache,
    private val diskCache: DiskImageCache
) {



    private val downloadSemaphore = Semaphore(3) // max 3 concurrent downloads


    suspend fun load(
        url: String,
        reqWidth: Int, reqHeight: Int
    ): Bitmap = withContext(Dispatchers.IO) {

        memoryCache.get(url)?.let { return@withContext it }



        diskCache.get(url)?.let {
            memoryCache.put(url, it)
            return@withContext it
        }

        downloadSemaphore.acquire()

        try {
            val bitmap = download(url, reqHeight = reqHeight, reqWidth = reqWidth)
            diskCache.put(url, bitmap)
            memoryCache.put(url, bitmap)
            bitmap
        } finally {
            downloadSemaphore.release()
        }
    }

//    private fun download(url: String): Bitmap {
//        val connection = URL(url).openConnection() as HttpURLConnection
//        connection.connect()
//        return BitmapFactory.decodeStream(connection.inputStream)
//    }

    private fun download(url: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.doInput = true
        connection.connect()

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(connection.inputStream, null, options)

        // calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false

        // Need to reconnect to get the stream again
        val conn2 = URL(url).openConnection() as HttpURLConnection
        conn2.connectTimeout = 30000
        conn2.readTimeout = 30000
        conn2.doInput = true
        conn2.connect()
        return BitmapFactory.decodeStream(conn2.inputStream, null, options)
            ?: throw IOException("Failed to decode bitmap from $url")
    }


    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
