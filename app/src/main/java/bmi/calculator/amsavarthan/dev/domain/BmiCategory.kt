package bmi.calculator.amsavarthan.dev.domain

import androidx.compose.ui.graphics.Color
import bmi.calculator.amsavarthan.dev.presentation.ui.theme.Green
import bmi.calculator.amsavarthan.dev.presentation.ui.theme.Orange
import bmi.calculator.amsavarthan.dev.presentation.ui.theme.Red
import bmi.calculator.amsavarthan.dev.presentation.ui.theme.Yellow

enum class BmiCategory(
    val color: Color,
    val range: ClosedFloatingPointRange<Double>
) {
    Underweight(Yellow, AppConstants.BMI_UNDERWEIGHT_RANGE),
    Normal(Green, AppConstants.BMI_NORMAL_RANGE),
    Overweight(Orange, AppConstants.BMI_OVERWEIGHT_RANGE),
    Obese(Red, AppConstants.BMI_OBESE_RANGE);

    companion object {
        fun from(bmi: Double): BmiCategory {
            return entries.first { category ->
                val value = bmi.coerceIn(
                    minimumValue = AppConstants.BMI_MIN_VALUE,
                    maximumValue = AppConstants.BMI_MAX_VALUE
                )
                category.range.contains(value)
            }
        }
    }

}