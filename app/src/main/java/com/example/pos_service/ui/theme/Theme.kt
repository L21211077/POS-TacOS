package com.example.pos_service.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGourmetColorScheme = darkColorScheme(
    primary = FlameRedPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A181C),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = AmberGoldSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF423512),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = AmberGoldLight,
    background = DarkBackground,
    onBackground = TextPrimaryWhite,
    surface = DarkSurface,
    onSurface = TextPrimaryWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryGray,
    outline = BorderDark,
    outlineVariant = BorderDark
)

@Composable
fun POSTacOSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkGourmetColorScheme,
        typography = Typography,
        content = content
    )
}
