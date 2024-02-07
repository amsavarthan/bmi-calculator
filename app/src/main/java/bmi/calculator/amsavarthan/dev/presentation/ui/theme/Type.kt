package bmi.calculator.amsavarthan.dev.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import bmi.calculator.amsavarthan.dev.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private fun varelaRoundFont(name: String = "Varela Round", weight: FontWeight): Font {
    return Font(
        googleFont = GoogleFont(name),
        fontProvider = provider,
        weight = weight
    )
}

private val fontFamily = FontFamily(
    varelaRoundFont(weight = FontWeight.W100),
    varelaRoundFont(weight = FontWeight.W200),
    varelaRoundFont(weight = FontWeight.W300),
    varelaRoundFont(weight = FontWeight.W400),
    varelaRoundFont(weight = FontWeight.W500),
    varelaRoundFont(weight = FontWeight.W600),
    varelaRoundFont(weight = FontWeight.W700),
    varelaRoundFont(weight = FontWeight.W800),
    varelaRoundFont(weight = FontWeight.W900),
)

val DefaultTypography = Typography()
val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(
        fontFamily = fontFamily
    ),
    displayMedium = DefaultTypography.displayMedium.copy(
        fontFamily = fontFamily
    ),
    displaySmall = DefaultTypography.displaySmall.copy(
        fontFamily = fontFamily
    ),

    headlineLarge = DefaultTypography.headlineLarge.copy(
        fontFamily = fontFamily
    ),
    headlineMedium = DefaultTypography.headlineMedium.copy(
        fontFamily = fontFamily
    ),
    headlineSmall = DefaultTypography.headlineSmall.copy(
        fontFamily = fontFamily
    ),

    titleLarge = DefaultTypography.titleLarge.copy(
        fontFamily = fontFamily
    ),
    titleMedium = DefaultTypography.titleMedium.copy(
        fontFamily = fontFamily
    ),
    titleSmall = DefaultTypography.titleSmall.copy(
        fontFamily = fontFamily
    ),

    bodyLarge = DefaultTypography.bodyLarge.copy(
        fontFamily = fontFamily
    ),
    bodyMedium = DefaultTypography.bodyMedium.copy(
        fontFamily = fontFamily
    ),
    bodySmall = DefaultTypography.bodySmall.copy(
        fontFamily = fontFamily
    ),

    labelLarge = DefaultTypography.labelLarge.copy(
        fontFamily = fontFamily
    ),
    labelMedium = DefaultTypography.labelMedium.copy(
        fontFamily = fontFamily
    ),
    labelSmall = DefaultTypography.labelSmall.copy(
        fontFamily = fontFamily
    ),
)