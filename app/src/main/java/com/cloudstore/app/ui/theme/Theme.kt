package com.cloudstore.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TechBlueDark,
    onPrimary = TechOnPrimaryDark,
    primaryContainer = TechPrimaryContainerDark,
    onPrimaryContainer = TechOnPrimaryContainerDark,
    secondary = TechSecondaryDark,
    secondaryContainer = TechSecondaryContainerDark,
    tertiary = TechTertiaryDark,
    tertiaryContainer = TechTertiaryContainerDark,
    background = TechBackgroundDark,
    onBackground = TechOnSurfaceDark,
    surface = TechSurfaceDark,
    onSurface = TechOnSurfaceDark,
    surfaceVariant = TechSurfaceVariantDark,
    onSurfaceVariant = TechOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = TechBlueLight,
    onPrimary = TechOnPrimaryLight,
    primaryContainer = TechPrimaryContainerLight,
    onPrimaryContainer = TechOnPrimaryContainerLight,
    secondary = TechSecondaryLight,
    secondaryContainer = TechSecondaryContainerLight,
    tertiary = TechTertiaryLight,
    tertiaryContainer = TechTertiaryContainerLight,
    background = TechBackgroundLight,
    onBackground = TechOnSurfaceLight,
    surface = TechSurfaceLight,
    onSurface = TechOnSurfaceLight,
    surfaceVariant = TechSurfaceVariantLight,
    onSurfaceVariant = TechOnSurfaceVariantLight
)

@Composable
fun CloudStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
