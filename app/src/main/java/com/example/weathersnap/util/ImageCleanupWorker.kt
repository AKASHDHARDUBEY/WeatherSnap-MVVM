package com.example.weathersnap.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ImageCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val cacheDir = applicationContext.externalCacheDir
        val files = cacheDir?.listFiles()
        
        val currentTime = System.currentTimeMillis()
        files?.forEach { file ->
            if (file.name.contains("compressed_") && (currentTime - file.lastModified() > 86400000)) {
                file.delete()
            }
        }
        return Result.success()
    }
}
