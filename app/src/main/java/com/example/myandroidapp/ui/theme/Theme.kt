package com.example.myandroidapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalAppColorScheme = staticCompositionLocalOf {
    mutableStateOf(AppColorScheme.Green)
}

enum class AppColorScheme {
    Green,
    Purple,
    Beige,
    Pink
}

@Composable
fun AppColorScheme.toColorScheme(darkTheme: Boolean = isSystemInDarkTheme()) = when (this) {
    AppColorScheme.Green -> if (darkTheme) greenDarkColorScheme else greenLightColorScheme
    AppColorScheme.Purple -> if (darkTheme) purpleDarkColorScheme else purpleLightColorScheme
    AppColorScheme.Beige -> if (darkTheme) beigeDarkColorScheme else beigeLightColorScheme
    AppColorScheme.Pink -> if (darkTheme) pinkDarkColorScheme else pinkLightColorScheme
}

val WarningColor = Color(0xFFFFA726)

private val greenLightColorScheme = lightColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = LightGreen80,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

private val greenDarkColorScheme = darkColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = LightGreen40,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
)

private val purpleLightColorScheme = lightColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val purpleDarkColorScheme = darkColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val beigeLightColorScheme = lightColorScheme(
    primary = Color(0xFF8B7355),
    secondary = Color(0xFFD7CEC7),
    tertiary = Color(0xFFC09F80)
)

private val beigeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFC09F80),
    secondary = Color(0xFF8B7355),
    tertiary = Color(0xFFD7CEC7)
)

private val pinkLightColorScheme = lightColorScheme(
    primary = Color(0xFFE91E63),
    secondary = Color(0xFFF48FB1),
    tertiary = Color(0xFFF8BBD0)
)

private val pinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFFF48FB1),
    secondary = Color(0xFFE91E63),
    tertiary = Color(0xFFF8BBD0)
)

@Composable
fun PlantCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    colorScheme: AppColorScheme = AppColorScheme.Green,
    content: @Composable () -> Unit
) {
    val selectedScheme by if (colorScheme == AppColorScheme.Green) {
        remember { mutableStateOf(AppColorScheme.Green) }
    } else {
        LocalAppColorScheme.current
    }

    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> selectedScheme.toColorScheme(darkTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalAppColorScheme provides remember { mutableStateOf(selectedScheme) }
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}