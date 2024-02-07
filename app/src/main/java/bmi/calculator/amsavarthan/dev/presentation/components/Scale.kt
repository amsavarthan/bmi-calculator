package bmi.calculator.amsavarthan.dev.presentation.components

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.withTranslation
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A scale that can be used to select a value from a range of values.
 *
 * Logic:
 *
 * It works by calculating the drag offset and then rounding it to the nearest
 * multiple of spaceBetweenLines. This is done to make sure that the scale is in steps of
 * spaceBetweenLines.
 *
 * For example, if the spaceBetweenLines is 16dp, then the value will be in steps of 16 starting
 * from the min value.
 *
 * Min value = 20
 * Max value = 120
 *
 * If the current value is 21, then the offsetX will be 16dp that is (spaceBetweenLines * (21-20))
 *
 * @param currentValue The current value of the scale.
 * @param onValueChanged The callback that is called when the value is changed.
 * @param modifier The modifier to be applied to the scale.
 * @param minValue The minimum value of the scale.
 * @param maxValue The maximum value of the scale.
 * @param style The style for the scale.
 * @param verticalAlignment The vertical alignment of the scale.
 * @param horizontalAlignment The horizontal alignment of the scale.
 * @param orientation The orientation of the scale.
 * @param animateInitialValue Whether to animate the initial value.
 */
@Composable
fun Scale(
    currentValue: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 20,
    maxValue: Int = 120,
    style: ScaleStyle = ScaleStyle.Default,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    orientation: Orientation = Orientation.Horizontal,
    animateInitialValue: Boolean = true,
) {
    val density = LocalDensity.current
    val spaceBetweenLinesPx = with(density) { style.spaceBetweenLines.toPx() }

    val maxHeight = with(style) {
        maxOf(tenthLineHeight, fifthLineHeight, otherLineHeight)
    }
    val numberingHeight = maxHeight + 36.dp

    val scope = rememberCoroutineScope()
    var scaleCenter by remember { mutableStateOf(Offset.Zero) }
    var internalCurrentValue by remember {
        mutableIntStateOf(currentValue.coerceIn(minValue, maxValue))
    }

    fun getOffsetXFor(number: Int): Float {
        val numberOfLines = number - minValue
        return spaceBetweenLinesPx * numberOfLines * -1
    }

    fun getValueAt(offsetX: Float): Int {
        val numberOfLines = (offsetX / spaceBetweenLinesPx).roundToInt()
        return minValue + numberOfLines
    }

    //Initially set to 1.35 times the current value for a smooth animation
    val dragOffset = remember {
        val initialOffset = if (animateInitialValue) 1.35f else 1f
        Animatable(getOffsetXFor(internalCurrentValue) * initialOffset)
    }

    val startOffset = 0f
    val endOffset = getOffsetXFor(maxValue)

    //Animate to the current value
    if (animateInitialValue) {
        LaunchedEffect(Unit) {
            dragOffset.animateTo(getOffsetXFor(internalCurrentValue), tween(1000))
        }
    }

    LaunchedEffect(internalCurrentValue) {
        onValueChanged(internalCurrentValue)
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { _, draggedAmount ->
                val dragAmount = when (orientation) {
                    Orientation.Horizontal -> draggedAmount.x
                    Orientation.Vertical -> draggedAmount.y
                }

                val newDragOffset = (dragOffset.value + dragAmount).coerceIn(endOffset, startOffset)

                //Snap the value to the nearest multiple of spaceBetweenLines
                //because we want the scale to be in steps of spaceBetweenLines
                val snappedValue = newDragOffset
                    .snapToNearestMultipleOf(spaceBetweenLinesPx)
                    .coerceIn(endOffset, startOffset)

                //Calculate the current value based on the offset from the center
                val offsetX = scaleCenter.x - snappedValue
                internalCurrentValue = getValueAt(offsetX - scaleCenter.x)
                scope.launch { dragOffset.snapTo(newDragOffset) }
            }
        }
        .graphicsLayer {
            //Flipping the canvas for landscape mode
            if (orientation == Orientation.Vertical) {
                rotationZ = 90f
                rotationY = 180f
            }
        }) {
        scaleCenter = center
        val offsetY = when (orientation) {
            Orientation.Horizontal -> when (verticalAlignment) {
                Alignment.Top -> maxHeight.toPx()
                Alignment.CenterVertically -> center.y
                Alignment.Bottom -> size.height - numberingHeight.toPx()
                else -> center.y
            }

            else -> when (horizontalAlignment) {
                Alignment.Start -> maxHeight.toPx() + numberingHeight.toPx()
                Alignment.CenterHorizontally -> center.y - numberingHeight.toPx() / 2
                Alignment.End -> center.y
                else -> -center.y - numberingHeight.toPx() / 2
            }
        }

        drawContext.canvas.nativeCanvas.apply {
            //for nextValue  (26) offsetX must be center - spaceBetweenLines * (26-25)
            //for currentValue (25) offsetX must be center
            //for prevValue   (24) offsetX will be center - spaceBetweenLines * (25-24)

            //Adding additional lines to make sure that the scale is not empty
            val totalLines = (maxValue + 50) * 2

            repeat(totalLines) { index ->

                //Starts 0 from the center
                val number = (totalLines / 2) - index
                val offsetX = center.x - getOffsetXFor(number)

                val isSelected = number == internalCurrentValue
                val color = when {
                    isSelected -> style.selectedLineColor
                    else -> style.lineColor
                }
                val thickness = when {
                    isSelected -> style.selectedLineThickness
                    else -> style.lineThickness
                }

                fun drawLine(height: Dp) {
                    //Center aligning the lines
                    val centerAlign = (maxHeight - height) / 2

                    withTranslation(y = centerAlign.toPx()) {
                        //Offset to make sure that the line is in the center
                        val x = offsetX - (thickness / 2).toPx()
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(x, offsetY),
                            cornerRadius = CornerRadius(2.dp.toPx()),
                            size = Size(
                                width = thickness.toPx(), height = height.toPx()
                            )
                        )
                    }
                }

                withTranslation(x = dragOffset.value) {
                    when {
                        number % 10 == 0 -> drawLine(style.tenthLineHeight)
                        number % 5 == 0 -> drawLine(style.fifthLineHeight)
                        else -> {
                            drawLine(style.otherLineHeight)
                            return@withTranslation
                        }
                    }

                    val degrees = when (orientation) {
                        Orientation.Horizontal -> 0f
                        Orientation.Vertical -> -90f
                    }

                    if (!style.showNumbers) return@repeat

                    //Adjustment value is used to adjust the text position
                    //to make it look like it is in the center of the line
                    val xAdjustmentValue = 9.dp
                    val yAdjustmentValue = 3.dp

                    val numberOffsetX = offsetX
                    val numberOffsetY = offsetY + numberingHeight.toPx()

                    withTransform({
                        if (orientation == Orientation.Horizontal) return@withTransform
                        scale(1f, -1f, numberOffsetX, numberOffsetY)
                        rotate(degrees, numberOffsetX, numberOffsetY)
                    }) {
                        drawText(
                            number.toString(),
                            numberOffsetX - xAdjustmentValue.toPx(),
                            numberOffsetY + yAdjustmentValue.toPx(),
                            Paint().apply {
                                textSize = style.numberTextSize.toPx()
                                this.color = color.toArgb()
                            }
                        )
                    }
                }
            }
        }
    }
}

data class ScaleStyle(
    val lineColor: Color,
    val selectedLineColor: Color,
    val lineThickness: Dp,
    val selectedLineThickness: Dp,
    val tenthLineHeight: Dp,
    val fifthLineHeight: Dp,
    val otherLineHeight: Dp,
    val spaceBetweenLines: Dp,
    val showNumbers: Boolean,
    val numberTextSize: TextUnit
) {
    companion object {
        val Default: ScaleStyle
            @Composable get() = ScaleStyle(
                lineColor = MaterialTheme.colorScheme.onSurface,
                selectedLineColor = MaterialTheme.colorScheme.primary,
                lineThickness = 2.dp,
                selectedLineThickness = 4.dp,
                tenthLineHeight = 80.dp,
                fifthLineHeight = 80.dp,
                otherLineHeight = 40.dp,
                spaceBetweenLines = 16.dp,
                numberTextSize = 16.sp,
                showNumbers = true,
            )
    }
}

private fun Float.snapToNearestMultipleOf(number: Float): Float {
    return (this / number).roundToInt() * number
}
