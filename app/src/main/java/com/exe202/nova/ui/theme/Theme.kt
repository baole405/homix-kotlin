package com.exe202.nova.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NovaPrimary,
    onPrimary = NovaOnPrimary,
    secondary = NovaSecondary,
    background = NovaBackground,
    surface = NovaSurface
)

private val ManagerColorScheme = lightColorScheme(
    primary = ManagerPrimary,
    onPrimary = ManagerOnPrimary,
    secondary = ManagerSecondary,
    background = ManagerBackground,
    surface = ManagerSurface
)

@Composable
fun NovaTheme(
    isManager: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isManager) ManagerColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = NovaTypography,
        content = content
    )
}
