package com.exe202.nova.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

private val ShadcnLightColorScheme = lightColorScheme(
    background = LightBackground,
    onBackground = LightForeground,
    surface = LightCard,
    onSurface = LightForeground,
    surfaceVariant = LightMuted,
    onSurfaceVariant = LightMutedForeground,
    primary = LightPrimary,
    onPrimary = LightPrimaryForeground,
    secondary = LightSecondary,
    onSecondary = LightSecondaryForeground,
    error = LightDestructive,
    onError = LightPrimaryForeground,
    outline = LightBorder,
    outlineVariant = LightCardBorder,
    inverseSurface = LightRing
)

private val ShadcnDarkColorScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkForeground,
    surface = DarkCard,
    onSurface = DarkForeground,
    surfaceVariant = DarkMuted,
    onSurfaceVariant = DarkMutedForeground,
    primary = DarkPrimary,
    onPrimary = DarkPrimaryForeground,
    secondary = DarkSecondary,
    onSecondary = DarkSecondaryForeground,
    error = DarkDestructive,
    onError = DarkPrimaryForeground,
    outline = DarkBorder,
    outlineVariant = DarkCardBorder,
    inverseSurface = DarkRing
)

// CompositionLocals for theme state propagation
val LocalIsDarkTheme = staticCompositionLocalOf { false }
val LocalToggleTheme = compositionLocalOf<() -> Unit> { {} }

@Composable
fun NovaTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) ShadcnDarkColorScheme else ShadcnLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NovaTypography,
        shapes = NovaShapes,
        content = content
    )
}
