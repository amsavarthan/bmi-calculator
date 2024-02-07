package bmi.calculator.amsavarthan.dev.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import android.os.Build

class BitmapHelper(context: Context) {
    private val cache = Cache(context)

    fun saveToCacheAndGetUri(
        bitmap: Bitmap,
        fileNameWithExtension: String = AppConstants.TEMP_FILE_NAME_WITH_EXTENSION
    ): Uri {
        return cache.saveAndGetUri(bitmap, fileNameWithExtension)
    }

    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Bitmap.createBitmap(picture)
        } else {
            val bitmap = Bitmap.createBitmap(
                picture.width,
                picture.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawPicture(picture)
            bitmap
        }
        return bitmap
    }
}