package com.flexaura.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

object AppDimensions {
    val paddingExtraSmall = 4.dp
    val paddingSmall = 8.dp
    val paddingMedium = 12.dp
    val paddingLarge = 16.dp
    val paddingExtraLarge = 32.dp
    val cornerRadiusSmall = 8.dp
    val cornerRadiusLarge = 16.dp
    val iconSizeSmall = 18.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val commentBarHeight = 42.dp
    val profileImageSizeSmall = 48.dp
    val profileImageSizeLarge = 120.dp
    val navigationBarHeight = 100.dp
    val userIdentityMaxWidth = 250.dp
    val imagePreviewSize = 410.dp
}

val LocalDimensions = staticCompositionLocalOf { AppDimensions }