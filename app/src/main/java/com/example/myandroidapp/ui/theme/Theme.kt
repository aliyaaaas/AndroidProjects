package com.example.myandroidapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppColorScheme = staticCompositionLocalOf {
    mutableStateOf(AppColorScheme.Purple)
}

enum class AppColorScheme {Purple, Beige, Pink}

@Composable
fun AppColorScheme.toMaterialColors(): androidx.compose.material.Colors {
    return when (this) {
        AppColorScheme.Purple -> lightColors(
            primary = Purple500,
            primaryVariant = Purple700,
            secondary = Teal200,
            background = Color.White,
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
        AppColorScheme.Beige -> lightColors(
            primary = BeigePrimary,
            primaryVariant = BeigePrimary,
            secondary = BeigeSecondary,
            background = BeigeBackground,
            surface = BeigeBackground,
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
        AppColorScheme.Pink -> lightColors(
            primary = PinkPrimary,
            primaryVariant = PinkPrimary,
            secondary = PinkSecondary,
            background = PinkBackground,
            surface = PinkBackground,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }
}

@Composable
fun MyAndroidAppTheme(
    content: @Composable () -> Unit
) {
    val selectedScheme by LocalAppColorScheme.current
    val colors = selectedScheme.toMaterialColors()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}