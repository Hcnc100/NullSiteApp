package com.nullpointer.nullsiteadmin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun textStyle(size: Int, lineHeight: Int, medium: Boolean = false, tracking: Float = 0f) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = if (medium) FontWeight.Medium else FontWeight.Normal,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
)

// System font, no download or added font asset; every role supports font scaling.
val AppTypography = Typography(
    displayLarge = textStyle(57, 64, tracking = -0.25f),
    displayMedium = textStyle(45, 52),
    displaySmall = textStyle(36, 44),
    headlineLarge = textStyle(32, 40),
    headlineMedium = textStyle(28, 36),
    headlineSmall = textStyle(24, 32),
    titleLarge = textStyle(22, 28),
    titleMedium = textStyle(16, 24, medium = true, tracking = 0.15f),
    titleSmall = textStyle(14, 20, medium = true, tracking = 0.1f),
    bodyLarge = textStyle(16, 24, tracking = 0.5f),
    bodyMedium = textStyle(14, 20, tracking = 0.25f),
    bodySmall = textStyle(12, 16, tracking = 0.4f),
    labelLarge = textStyle(14, 20, medium = true, tracking = 0.1f),
    labelMedium = textStyle(12, 16, medium = true, tracking = 0.5f),
    labelSmall = textStyle(11, 16, medium = true, tracking = 0.5f),
)
