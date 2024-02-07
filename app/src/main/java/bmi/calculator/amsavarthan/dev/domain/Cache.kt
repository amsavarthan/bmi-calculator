package bmi.calculator.amsavarthan.dev.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class Cache(private val context: Context) {

    fun saveAndGetUri(
        bitmap: Bitmap,
        fileNameWithExtension: String = AppConstants.TEMP_FILE_NAME_WITH_EXTENSION
    ): Uri {
        saveToImgCache(bitmap, fileNameWithExtension)
        return getUriFromImageCache(fileNameWithExtension)
    }

    private fun saveToImgCache(
        bitmap: Bitmap,
        fileNameWithExtension: String = AppConstants.TEMP_FILE_NAME_WITH_EXTENSION
    ) {
        val cacheImagesDir = File(context.cacheDir, AppConstants.TEMP_IMAGES_FOLDER_NAME)
        if (!cacheImagesDir.exists()) cacheImagesDir.mkdirs()
        val file = File(cacheImagesDir, fileNameWithExtension)
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
        }
    }

    private fun getUriFromImageCache(fileNameWithExtension: String): Uri {
        val cacheImagesDir = File(context.cacheDir, AppConstants.TEMP_IMAGES_FOLDER_NAME)
        if (!cacheImagesDir.exists()) cacheImagesDir.mkdirs()
        val file = File(cacheImagesDir, fileNameWithExtension)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

}