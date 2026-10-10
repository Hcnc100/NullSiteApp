package com.nullpointer.nullsiteadmin.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Explicit semantic roles: no inherited purple defaults or wallpaper-dependent colors.
internal val AppLightColorScheme = lightColorScheme(
    primary = Color(0xFF006875), onPrimary = Color.White,
    primaryContainer = Color(0xFF9BEFFF), onPrimaryContainer = Color(0xFF001F25),
    inversePrimary = Color(0xFF4FD8EB),
    secondary = Color(0xFF4B6268), onSecondary = Color.White,
    secondaryContainer = Color(0xFFCEE7ED), onSecondaryContainer = Color(0xFF061F24),
    tertiary = Color(0xFF555D7E), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCE1FF), onTertiaryContainer = Color(0xFF111A37),
    background = Color(0xFFF5FAFB), onBackground = Color(0xFF152023),
    surface = Color(0xFFF5FAFB), onSurface = Color(0xFF152023),
    surfaceVariant = Color(0xFFDBE4E7), onSurfaceVariant = Color(0xFF3F484B),
    surfaceTint = Color(0xFF006875),
    inverseSurface = Color(0xFF2B3538), inverseOnSurface = Color(0xFFECF2F4),
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF6F797C), outlineVariant = Color(0xFFBFC8CB), scrim = Color.Black,
    surfaceBright = Color(0xFFF5FAFB), surfaceDim = Color(0xFFD5DBDD),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFEFF5F7),
    surfaceContainer = Color(0xFFE9EFF1), surfaceContainerHigh = Color(0xFFE3E9EB),
    surfaceContainerHighest = Color(0xFFDDE3E5),
)

internal val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FD8EB), onPrimary = Color(0xFF00363E),
    primaryContainer = Color(0xFF004F59), onPrimaryContainer = Color(0xFF9BEFFF),
    inversePrimary = Color(0xFF006875),
    secondary = Color(0xFFB2CBD1), onSecondary = Color(0xFF1D343A),
    secondaryContainer = Color(0xFF344B51), onSecondaryContainer = Color(0xFFCEE7ED),
    tertiary = Color(0xFFBDC5EB), onTertiary = Color(0xFF272F4D),
    tertiaryContainer = Color(0xFF3D4565), onTertiaryContainer = Color(0xFFDCE1FF),
    background = Color(0xFF0E171A), onBackground = Color(0xFFDDE3E5),
    surface = Color(0xFF0E171A), onSurface = Color(0xFFDDE3E5),
    surfaceVariant = Color(0xFF3F484B), onSurfaceVariant = Color(0xFFBFC8CB),
    surfaceTint = Color(0xFF4FD8EB),
    inverseSurface = Color(0xFFDDE3E5), inverseOnSurface = Color(0xFF2B3538),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF899396), outlineVariant = Color(0xFF3F484B), scrim = Color.Black,
    surfaceBright = Color(0xFF343D40), surfaceDim = Color(0xFF0E171A),
    surfaceContainerLowest = Color(0xFF091214), surfaceContainerLow = Color(0xFF172124),
    surfaceContainer = Color(0xFF1B2528), surfaceContainerHigh = Color(0xFF253033),
    surfaceContainerHighest = Color(0xFF303B3E),
)
