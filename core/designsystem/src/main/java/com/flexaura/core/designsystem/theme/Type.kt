package com.flexaura.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.flexaura.core.designsystem.R

val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
)

// Default Material 3 typography values
val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = SpaceGrotesk),
    displayMedium = baseline.displayMedium.copy(fontFamily = SpaceGrotesk),
    displaySmall = baseline.displaySmall.copy(fontFamily = SpaceGrotesk),

    headlineLarge = baseline.headlineLarge.copy(fontFamily = SpaceGrotesk),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = SpaceGrotesk),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = SpaceGrotesk),

    titleLarge = baseline.titleLarge.copy(fontFamily = SpaceGrotesk),
    titleMedium = baseline.titleMedium.copy(fontFamily = SpaceGrotesk),
    titleSmall = baseline.titleSmall.copy(fontFamily = SpaceGrotesk),

    bodyLarge = baseline.bodyLarge.copy(fontFamily = PlusJakartaSans),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = PlusJakartaSans),
    bodySmall = baseline.bodySmall.copy(fontFamily = PlusJakartaSans),

    labelLarge = baseline.labelLarge.copy(fontFamily = PlusJakartaSans),
    labelMedium = baseline.labelMedium.copy(fontFamily = PlusJakartaSans),
    labelSmall = baseline.labelSmall.copy(fontFamily = PlusJakartaSans),
)
