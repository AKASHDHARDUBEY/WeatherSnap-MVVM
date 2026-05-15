package com.example.weathersnap.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun compressImage(context: Context, originalFile: File): Pair<File, Long> {
        val originalSize = originalFile.length()
        val compressedFile = File(context.cacheDir, "compressed_${originalFile.name}")
        
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        val outputStream = FileOutputStream(compressedFile)
        
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.close()
        
        return Pair(compressedFile, compressedFile.length())
    }

    fun getFileSizeInKB(size: Long): String {
        return "${size / 1024} KB"
    }
}
