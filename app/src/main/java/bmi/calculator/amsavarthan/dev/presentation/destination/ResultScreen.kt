package bmi.calculator.amsavarthan.dev.presentation.destination

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.createChooser
import android.content.res.Configuration
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import bmi.calculator.amsavarthan.dev.R
import bmi.calculator.amsavarthan.dev.domain.AppConstants
import bmi.calculator.amsavarthan.dev.domain.BitmapHelper
import bmi.calculator.amsavarthan.dev.domain.BmiCategory
import bmi.calculator.amsavarthan.dev.presentation.MainUiEvent
import bmi.calculator.amsavarthan.dev.presentation.MainViewModel
import bmi.calculator.amsavarthan.dev.presentation.components.Button
import bmi.calculator.amsavarthan.dev.presentation.destination.destinations.WeightScreenDestination
import bmi.calculator.amsavarthan.dev.presentation.ui.theme.BMICalculatorTheme
import bmi.calculator.amsavarthan.dev.presentation.util.captureIntoPicture
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ResultScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val bitmapHelper = remember { BitmapHelper(context) }

    val writeStorageAccessState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // No permissions are needed on Android 10+ to add files in the shared storage
            emptyList()
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    )

    fun checkPermissionAndCreateBitmap(picture: Picture) {
        if (writeStorageAccessState.allPermissionsGranted) {
            viewModel.createBitmap(picture, bitmapHelper)
        } else if (writeStorageAccessState.shouldShowRationale) {
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "The storage permission is needed to save the image",
                    actionLabel = "Grant Access"
                )

                if (result == SnackbarResult.ActionPerformed) {
                    writeStorageAccessState.launchMultiplePermissionRequest()
                }
            }
        } else {
            writeStorageAccessState.launchMultiplePermissionRequest()
        }
    }

    fun shareBitmap(context: Context, uri: Uri, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(context, createChooser(intent, "Share your image"), null)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.onEach { event ->
            when (event) {
                is MainUiEvent.ShareImage -> {
                    val message = AppConstants.SHARE_MESSAGE.format(context.packageName)
                    shareBitmap(context, event.uri, message)
                }

                is MainUiEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }.launchIn(this)
    }

    ResultScreenContent(
        bmi = viewModel.bmiResult,
        snackbarHostState = snackbarHostState,
        onShare = { picture -> checkPermissionAndCreateBitmap(picture) },
        onInfoClick = {
            val websiteUri = context.getString(R.string.bmi_info_url).toUri()
            val intent = Intent(Intent.ACTION_VIEW, websiteUri)
            context.startActivity(intent)
        },
        onTryAgain = {
            navigator.navigate(WeightScreenDestination) {
                popUpTo(NavGraphs.root.startAppDestination) {
                    inclusive = true
                }
            }
        }
    )
}

@Composable
fun ResultScreenContent(
    bmi: Double,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onTryAgain: () -> Unit = {},
    onShare: (Picture) -> Unit = {},
    onInfoClick: () -> Unit = {},
) {
    val view = LocalView.current
    val screenOrientation = LocalConfiguration.current.orientation

    val picture = remember { Picture() }
    val bmiCategory = remember(bmi) { BmiCategory.from(bmi) }

    val bmiValue = remember {
        val differenceFactor = if (view.isInEditMode) 0.0 else 5.0
        val initialValue = ((bmi - differenceFactor).toFloat()).coerceAtLeast(0f)
        Animatable(initialValue)
    }

    LaunchedEffect(Unit) {
        bmiValue.animateTo(bmi.toFloat(), tween(1000, easing = LinearEasing))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val actionButtons = movableContentOf {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(),
                    onClick = { onShare(picture) },
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.btn_share),
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(imageVector = Icons.Rounded.Share, contentDescription = null)
                    }
                }
                Button(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(),
                    onClick = onTryAgain
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.btn_try_again),
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
                    }
                }
            }
        }
        val bmiResultInfo = movableContentOf {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.bmi_result_headline),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = AppConstants.DECIMAL_FORMAT.format(bmiValue.value),
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.bmi_result_unit))
                Spacer(modifier = Modifier.height(24.dp))
                BmiScale(value = bmi)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.bmi_result_category_message))
                        withStyle(SpanStyle(bmiCategory.color)) {
                            append(
                                bmiCategory.name.lowercase().replaceFirstChar(Char::uppercase)
                            )
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .consumeWindowInsets(padding),
            contentAlignment = Alignment.Center
        ) {
            when (screenOrientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 36.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            bmiResultInfo()
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            actionButtons()
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 36.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            bmiResultInfo()
                        }
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            actionButtons()
                        }
                    }
                }
            }

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                onClick = onInfoClick
            ) {
                Icon(imageVector = Icons.Rounded.Info, contentDescription = null)
            }

            // Shareable content for the image, this doesn't show in the UI
            BmiShareableResultInfoContent(bmi, picture)
        }
    }
}

@Composable
fun BmiShareableResultInfoContent(
    bmi: Double,
    picture: Picture,
    drawContent: Boolean = false
) {
    val bmiCategory = remember(bmi) { BmiCategory.from(bmi) }

    Box(modifier = Modifier.captureIntoPicture(picture, drawContent)) {
        Surface(
            modifier = Modifier.size(380.dp),
            color = Color.Black,
            contentColor = Color.White
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.bmi_result_headline_share),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = AppConstants.DECIMAL_FORMAT.format(bmi),
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.bmi_result_unit))
                    Spacer(modifier = Modifier.height(24.dp))
                    BmiScale(
                        value = bmi,
                        scaleLineColor = Color.Black,
                        scaleBorder = BorderStroke(2.dp, Color.White),
                        indicatorColor = Color.White,
                        animationEnabled = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.bmi_result_category_message_share))
                            withStyle(SpanStyle(bmiCategory.color)) {
                                append(
                                    bmiCategory.name.lowercase()
                                        .replaceFirstChar(Char::uppercase)
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.redirect_url),
                        color = Color.Gray
                    )
                }
                Image(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(bottom = 16.dp)
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                            alpha = 0.1f
                        },
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun BmiScale(
    value: Double,
    modifier: Modifier = Modifier,
    scaleLineColor: Color = Color.Black,
    scaleBorder: BorderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
    indicatorColor: Color = MaterialTheme.colorScheme.onSurface,
    cornerRadius: Dp = 24.dp,
    animationEnabled: Boolean = true,
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val indicatorPositionX = remember { Animatable(0f) }

    if (animationEnabled) {
        LaunchedEffect(Unit) {
            indicatorPositionX.animateTo(
                calculateIndicatorPositionX(
                    singlePartWidth = canvasSize.width / BmiCategory.entries.size,
                    value = value
                ),
                tween(1000)
            )
        }
    }

    Canvas(
        modifier = modifier.requiredSize(
            width = 240.dp,
            height = 16.dp
        )
    ) {
        canvasSize = size

        //Drawing background
        val singlePartWidth = size.width / BmiCategory.entries.size

        drawRoundRect(
            scaleBorder.brush,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(width = scaleBorder.width.toPx())
        )

        val scaleHorizontalPadding = 24.dp.toPx()
        BmiCategory.entries.forEachIndexed { index, category ->

            val leftRadius = when (index) {
                0 -> CornerRadius(cornerRadius.toPx())
                else -> CornerRadius.Zero
            }

            val rightRadius = when (index) {
                BmiCategory.entries.size - 1 -> CornerRadius(cornerRadius.toPx())
                else -> CornerRadius.Zero
            }

            val rect = Rect(
                topLeft = Offset(singlePartWidth * index, 0f),
                bottomRight = Offset(singlePartWidth * (index + 1), size.height)
            )

            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = rect.left,
                        top = rect.top,
                        right = rect.right,
                        bottom = rect.bottom,
                        topLeftCornerRadius = leftRadius,
                        bottomLeftCornerRadius = leftRadius,
                        topRightCornerRadius = rightRadius,
                        bottomRightCornerRadius = rightRadius
                    )
                )
            }

            drawPath(path, category.color)
        }

        //Drawing scale lines

        val scaleSize = Size(
            width = size.width - (scaleHorizontalPadding * 2),
            height = size.height
        )

        val totalLines = 30
        val lineSpacing = scaleSize.width / totalLines

        for (i in 0..totalLines) {
            val number = (i + 1 * 10) / 10.0
            val isWholeNumber = number % 1 == 0.0

            val lineStartY = when (isWholeNumber) {
                true -> size.height / 3f
                else -> size.height / 1.5f
            }
            val lineStartX = scaleHorizontalPadding + (lineSpacing * i)

            drawLine(
                color = scaleLineColor,
                start = Offset(lineStartX, lineStartY),
                end = Offset(lineStartX, size.height),
                strokeWidth = 2f
            )
        }

        val xPosResolvedValue = when (animationEnabled) {
            true -> indicatorPositionX.value
            else -> calculateIndicatorPositionX(
                singlePartWidth = singlePartWidth,
                value = value
            )
        }

        val xPos = xPosResolvedValue.coerceIn(
            minimumValue = scaleHorizontalPadding,
            maximumValue = size.width - scaleHorizontalPadding
        )

        drawLine(
            color = indicatorColor,
            start = Offset(
                x = xPos,
                y = -5.dp.toPx()
            ),
            end = Offset(
                x = xPos,
                y = size.height + 5.dp.toPx()
            ),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

    }
}

/**
 * Calculates the position of the indicator on the scale.
 *
 * Logic:
 *
 * If value is 5 with range 0..10 and scale width is 100, then indicator position will be 50 (50% of scale width).
 */
private fun calculateIndicatorPositionX(
    singlePartWidth: Float,
    value: Double,
): Float {
    val category = BmiCategory.from(value)
    val index = BmiCategory.entries.indexOf(category)

    val startX = singlePartWidth * index
    val endX = singlePartWidth * (index + 1)
    val categoryWidth = endX - startX

    val categoryStart = category.range.start
    val categoryEnd = category.range.endInclusive

    val valueRelativeToCategory = value - categoryStart
    val categoryRange = categoryEnd - categoryStart

    val categoryWidthFactor = valueRelativeToCategory / categoryRange
    val indicatorPositionX = startX + (categoryWidth * categoryWidthFactor)

    return indicatorPositionX.toFloat()
}

@Preview
@Composable
fun ResultScreenPreview() {
    BMICalculatorTheme {
        ResultScreenContent(
            bmi = BmiCategory.Normal.range.start,
        )
    }
}

@Preview
@Composable
fun Share_Content() {
    BMICalculatorTheme {
        BmiShareableResultInfoContent(
            bmi = BmiCategory.Normal.range.start,
            picture = Picture(),
            drawContent = true
        )
    }
}