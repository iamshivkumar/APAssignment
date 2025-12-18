package com.example.apassignment.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class DiskImageCache(context: Context) {

    private val cacheDir = File(context.applicationContext.cacheDir, "images").apply {
        if (!exists()) mkdirs()
    }

    fun get(key: String): Bitmap? {
        val file = File(cacheDir, key.md5())
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    fun put(key: String, bitmap: Bitmap) {
        val file = File(cacheDir, key.md5())
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}

fun String.md5(): String =
    MessageDigest.getInstance("MD5")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }
