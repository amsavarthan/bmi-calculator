package bmi.calculator.amsavarthan.dev.presentation.destination

import android.content.res.Configuration
import android.media.AudioManager
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import bmi.calculator.amsavarthan.dev.R
import bmi.calculator.amsavarthan.dev.domain.AppConstants
import bmi.calculator.amsavarthan.dev.presentation.MainViewModel
import bmi.calculator.amsavarthan.dev.presentation.components.Button
import bmi.calculator.amsavarthan.dev.presentation.components.CircularScale
import bmi.calculator.amsavarthan.dev.presentation.destination.destinations.HeightScreenDestination
import bmi.calculator.amsavarthan.dev.presentation.ui.theme.BMICalculatorTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun WeightScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel,
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val audioManager = remember {
        ContextCompat.getSystemService(context, AudioManager::class.java)
    }

    LaunchedEffect(viewModel.weightInKg) {
        audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1f)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    WeightScreenContent(
        currentWeight = viewModel.weightInKg,
        onWeightChanged = viewModel::updateWeight,
        onContinue = {
            navigator.navigate(HeightScreenDestination)
        }
    )
}

@Composable
fun WeightScreenContent(
    currentWeight: Int,
    onWeightChanged: (Int) -> Unit,
    onContinue: () -> Unit = {}
) {
    val screenOrientation = LocalConfiguration.current.orientation

    Surface(modifier = Modifier.fillMaxSize()) {
        val continueButton = movableContentOf {
            Button(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp),
                onClick = onContinue
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.btn_continue),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
        val informationInterface = movableContentOf {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.weight_screen_headline),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterVertically
                    ),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 4.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            text = currentWeight.toString(),
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            color = MaterialTheme.colorScheme.outline,
                            text = stringResource(R.string.weight_unit),
                        )
                    }
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.outline
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                        ) {
                            val weightInPounds = currentWeight * 2.2
                            Text(
                                modifier = Modifier.alignByBaseline(),
                                text = "${weightInPounds.roundToInt()}"
                            )
                            Text(
                                modifier = Modifier.alignByBaseline(),
                                text = stringResource(R.string.weight_unit_alt)
                            )
                        }
                    }
                }

                if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    continueButton()
                }
            }
        }
        val weightScale = @Composable {
            val orientation = when (screenOrientation) {
                Configuration.ORIENTATION_LANDSCAPE -> Orientation.Vertical
                else -> Orientation.Horizontal
            }
            CircularScale(
                modifier = Modifier.offset {
                    if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        return@offset IntOffset(-80.dp.roundToPx(), 0.dp.roundToPx())
                    }
                    IntOffset.Zero
                },
                currentValue = currentWeight,
                onValueChange = onWeightChanged,
                orientation = orientation
            )
        }

        when (screenOrientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight()
                    ) {
                        informationInterface()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        weightScale()
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(bottom = 36.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        informationInterface()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        weightScale()
                    }
                    continueButton()
                }
            }
        }
    }
}

@Preview
@Composable
fun WeightScreenPreview() {
    BMICalculatorTheme {
        WeightScreenContent(
            currentWeight = AppConstants.INITIAL_WEIGHT,
            onWeightChanged = {}
        )
    }
}