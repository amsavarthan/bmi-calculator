package bmi.calculator.amsavarthan.dev.presentation.util

import android.graphics.Picture
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BuildDrawCacheParams
import androidx.compose.ui.draw.DrawCacheModifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

class CaptureIntoPicture(
    private val picture: Picture,
    private val drawContent: Boolean = true,
) : DrawCacheModifier {

    private var width: Int = 0
    private var height: Int = 0

    override fun onBuildCache(params: BuildDrawCacheParams) {
        width = params.size.width.toInt()
        height = params.size.height.toInt()
    }

    override fun ContentDrawScope.draw() {
        val pictureCanvas = androidx.compose.ui.graphics.Canvas(
            picture.beginRecording(width, height)
        )

        draw(this, layoutDirection, pictureCanvas, size) internalDraw@{
            this@draw.drawContent()
        }

        picture.endRecording()

        if (!drawContent) return
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawPicture(picture)
        }
    }
}

fun Modifier.captureIntoPicture(picture: Picture, drawContent: Boolean = true): Modifier {
    return this.then(CaptureIntoPicture(picture, drawContent))
}