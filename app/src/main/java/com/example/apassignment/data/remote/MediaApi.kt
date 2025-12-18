package com.example.apassignment.data.remote

import android.util.Log
import com.example.apassignment.domain.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MediaApi {

    suspend fun fetchMedia(limit: Int, offset: Int): List<MediaItem> =
        withContext(Dispatchers.IO) {

            val url =
                URL("https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=$limit&offset=$offset")

            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            if (connection.responseCode != 200) {
                throw IOException("HTTP ${connection.responseCode}")
            }


            val json = connection.inputStream.bufferedReader().use { it.readText() }
            parseResponse(json)
        }

    private fun parseResponse(json: String): List<MediaItem> {
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            val thumb = obj.getJSONObject("thumbnail")

            MediaItem(
                id = obj.getString("id"),
                imageUrl = "${thumb.getString("domain")}/" +
                        "${thumb.getString("basePath")}/0/" +
                        thumb.getString("key")
            )
        }
    }
}
