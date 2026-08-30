package com.chujichengnianren.guide.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = ActionBlue,
    onPrimary = Sheet,
    primaryContainer = ActionBlueContainer,
    onPrimaryContainer = Ink,
    secondary = Moss,
    onSecondary = Sheet,
    secondaryContainer = Mint,
    onSecondaryContainer = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Sheet,
    onSurface = Ink,
    surfaceVariant = Mint,
    onSurfaceVariant = Moss,
    outline = Divider,
    outlineVariant = Divider,
    error = EmergencyOrange,
    onError = Sheet,
    errorContainer = EmergencyContainer,
    onErrorContainer = Ink,
)

private val DarkColors = darkColorScheme(
    primary = ActionBlueDark,
    onPrimary = ColorTokens.DarkBlueInk,
    primaryContainer = ActionBlueContainerDark,
    onPrimaryContainer = InkOnDark,
    secondary = MossLight,
    onSecondary = PaperDark,
    secondaryContainer = MintDark,
    onSecondaryContainer = InkOnDark,
    background = PaperDark,
    onBackground = InkOnDark,
    surface = SheetDark,
    onSurface = InkOnDark,
    surfaceVariant = MintDark,
    onSurfaceVariant = MossLight,
    outline = DividerDark,
    outlineVariant = DividerDark,
    error = EmergencyOrange,
    onError = PaperDark,
    errorContainer = EmergencyContainerDark,
    onErrorContainer = InkOnDark,
)

private object ColorTokens {
    val DarkBlueInk = androidx.compose.ui.graphics.Color(0xFF142A5B)
}

@Composable
fun AdultStarterGuideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = GuideTypography,
        shapes = GuideShapes,
        content = content,
    )
}

