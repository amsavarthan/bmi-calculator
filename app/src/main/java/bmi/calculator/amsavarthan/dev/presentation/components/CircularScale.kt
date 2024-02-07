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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.withRotation
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


/**
 * A circular scale that can be used to select a value from a range of values.
 *
 * Logic:
 *
 * It works by drawing a larger circle with lines being drawn at the circumference of the circle.
 * The minValue value line must be place at 270 degrees of the bigger circle (startAngle - at the top center
 * since in computer graphics the scale is inverted).
 *
 * The cartesian coordinates (x,y) are calculated using the polar coordinates (r,theta).
 * x = r * cos(theta); y = r * sin(theta)
 *
 * As the user drags the scale, the difference between the current dragged angle and the start angle
 * is calculated and the scale is rotated by that angle. By subtracting the dragged angle to the minValue
 * we get the current value.
 *
 * currentValue = minValue - draggedAngle
 *
 * @param currentValue The current value of the scale.
 * @param onValueChange The callback that is called when the value is changed.
 * @param modifier The modifier to be applied to the scale.
 * @param minValue The minimum value of the scale.
 * @param maxValue The maximum value of the scale.
 * @param style The style for the scale.
 * @param orientation The orientation of the scale.
 * @param animateInitialValue Whether to animate the initial value.
 */
@Composable
fun CircularScale(
    currentValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 20,
    maxValue: Int = 120,
    style: CircularScaleStyle = CircularScaleStyle.Default,
    orientation: Orientation = Orientation.Horizontal,
    animateInitialValue: Boolean = true,
) {

    val scope = rememberCoroutineScope()

    var dragStartedAngle by remember { mutableFloatStateOf(0f) }

    val internalCurrentValue = currentValue.coerceIn(minValue, maxValue)
    val angleForCurrentValue = (minValue - internalCurrentValue).toFloat()
    val angle = remember {
        val initialValue = when (animateInitialValue) {
            true -> angleForCurrentValue + 20
            else -> angleForCurrentValue
        }.coerceIn(
            minimumValue = minValue - maxValue.toFloat(),
            maximumValue = minValue - minValue.toFloat()
        )
        Animatable(initialValue)
    }
    var oldAngle by remember { mutableFloatStateOf(angleForCurrentValue) }

    var circleCenter by remember { mutableStateOf(Offset.Zero) }

    if (animateInitialValue) {
        LaunchedEffect(Unit) {
            angle.animateTo(angleForCurrentValue, animationSpec = tween(1000))
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                if (orientation == Orientation.Horizontal) return@graphicsLayer
                rotationZ = 90f
                rotationY = 180f
                rotationX = 180f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartedAngle = getAngleFromOffset(circleCenter - offset)
                    },
                    onDragEnd = { oldAngle = angle.value }
                ) { change, _ ->
                    val touchAngle = getAngleFromOffset(circleCenter - change.position)
                    val draggedAngle = touchAngle - dragStartedAngle

                    //using oldAngle to avoid exponential increase in angle
                    val newAngle = (oldAngle + draggedAngle).coerceIn(
                        minimumValue = minValue - maxValue.toFloat(),
                        maximumValue = minValue - minValue.toFloat()
                    )
                    scope.launch { angle.snapTo(newAngle) }
                    onValueChange((minValue - newAngle).roundToInt())
                }
            }
    ) {

        val radiusPx = style.scaleRadius.toPx()
        val scaleWidthPx = style.scaleWidth.toPx()
        val scaleThicknessPx = style.scaleThickness.toPx()

        val outerRadius = radiusPx + (scaleWidthPx / 2)
        val innerRadius = radiusPx - (scaleWidthPx / 2)

        circleCenter = Offset(center.x, center.y + outerRadius)

        drawCircle(
            color = style.scaleStrokeColor,
            radius = outerRadius,
            center = circleCenter,
            style = Stroke(scaleThicknessPx)
        )

        drawCircle(
            color = style.scaleStrokeColor,
            radius = innerRadius,
            center = circleCenter,
            style = Stroke(scaleThicknessPx)
        )

        drawContext.canvas.nativeCanvas.apply {
            withRotation(
                degrees = angle.value,
                pivotX = circleCenter.x,
                pivotY = circleCenter.y
            ) {
                val startAngle = 270f //-90 degrees
                for (i in minValue..maxValue) {
                    //position minValue at startAngle
                    val angleInDeg = (i - minValue) + startAngle

                    val isSelected = i == internalCurrentValue

                    val lineColor = when {
                        isSelected -> style.selectedLineColor
                        i % 10 == 0 -> style.tenthLineColor
                        i % 5 == 0 -> style.fifthLineColor
                        else -> style.otherLineColor
                    }

                    val lineHeightPx = when {
                        i % 10 == 0 -> style.scaleWidth / 2.5f
                        i % 5 == 0 -> style.scaleWidth / 3.5f
                        else -> style.scaleWidth / 5.5f
                    }.toPx()

                    val strokeWidth = when (isSelected) {
                        true -> style.selectedLineThickness
                        else -> style.lineThickness
                    }.toPx()

                    val startOffset = getOffsetFromAngle(
                        degree = angleInDeg,
                        radius = outerRadius - style.lineThickness.toPx()
                    ) + circleCenter

                    val endOffset = getOffsetFromAngle(
                        degree = angleInDeg,
                        radius = outerRadius - lineHeightPx
                    ) + circleCenter

                    drawLine(
                        color = lineColor,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    val textStartOffset = getOffsetFromAngle(
                        degree = angleInDeg,
                        radius = outerRadius - lineHeightPx - 16.dp.toPx()
                    ) + circleCenter

                    if (!style.showNumbers) return@withRotation

                    withRotation(
                        degrees = angleInDeg + 90,
                        pivotX = textStartOffset.x,
                        pivotY = textStartOffset.y
                    ) {
                        if (i % 10 == 0) {
                            drawText(
                                i.toString(),
                                textStartOffset.x,
                                textStartOffset.y,
                                Paint().apply {
                                    textAlign = Paint.Align.CENTER
                                    textSize = 12.sp.toPx()
                                    color = lineColor.toArgb()
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

data class CircularScaleStyle(
    val scaleRadius: Dp,
    val scaleWidth: Dp,
    val scaleThickness: Dp,
    val scaleStrokeColor: Color,
    val tenthLineColor: Color,
    val fifthLineColor: Color,
    val otherLineColor: Color,
    val selectedLineColor: Color,
    val lineThickness: Dp,
    val selectedLineThickness: Dp,
    val showNumbers: Boolean,
) {
    companion object {
        val Default: CircularScaleStyle
            @Composable get() = CircularScaleStyle(
                scaleRadius = 600.dp,
                scaleWidth = 120.dp,
                scaleStrokeColor = MaterialTheme.colorScheme.onSurface,
                scaleThickness = 2.dp,
                tenthLineColor = MaterialTheme.colorScheme.onSurface,
                fifthLineColor = MaterialTheme.colorScheme.onSurface,
                otherLineColor = MaterialTheme.colorScheme.outline,
                selectedLineColor = MaterialTheme.colorScheme.primary,
                lineThickness = 2.dp,
                selectedLineThickness = 4.dp,
                showNumbers = true
            )
    }
}

private fun getAngleFromOffset(offset: Offset): Float {
    val theta = atan2(offset.y, offset.x) //in radians
    return Math.toDegrees(theta.toDouble()).toFloat()
}

private fun getOffsetFromAngle(degree: Float, radius: Float): Offset {
    //convert to radians since cos and sin takes radians instead of degrees
    val angleInRad = Math.toRadians(degree.toDouble())
    val x = (radius * cos(angleInRad)).toFloat()
    val y = (radius * sin(angleInRad)).toFloat()
    return Offset(x, y)
}