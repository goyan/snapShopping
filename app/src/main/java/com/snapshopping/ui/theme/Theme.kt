package com.snapshopping.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Primary colors - Fresh green for food app
private val PrimaryLight = Color(0xFF2E7D32)
private val OnPrimaryLight = Color.White
private val PrimaryContainerLight = Color(0xFFA5D6A7)
private val OnPrimaryContainerLight = Color(0xFF1B5E20)

private val SecondaryLight = Color(0xFF558B2F)
private val OnSecondaryLight = Color.White
private val SecondaryContainerLight = Color(0xFFC5E1A5)
private val OnSecondaryContainerLight = Color(0xFF33691E)

private val TertiaryLight = Color(0xFF00695C)
private val OnTertiaryLight = Color.White

private val SurfaceLight = Color(0xFFFAFAFA)
private val OnSurfaceLight = Color(0xFF1C1B1F)
private val SurfaceVariantLight = Color(0xFFE7E0EC)

private val ErrorLight = Color(0xFFB00020)

// Dark colors
private val PrimaryDark = Color(0xFF81C784)
private val OnPrimaryDark = Color(0xFF1B5E20)
private val PrimaryContainerDark = Color(0xFF2E7D32)
private val OnPrimaryContainerDark = Color(0xFFA5D6A7)

private val SecondaryDark = Color(0xFF9CCC65)
private val OnSecondaryDark = Color(0xFF33691E)
private val SecondaryContainerDark = Color(0xFF558B2F)
private val OnSecondaryContainerDark = Color(0xFFC5E1A5)

private val TertiaryDark = Color(0xFF4DB6AC)
private val OnTertiaryDark = Color(0xFF004D40)

private val SurfaceDark = Color(0xFF121212)
private val OnSurfaceDark = Color(0xFFE6E1E5)
private val SurfaceVariantDark = Color(0xFF49454F)

private val ErrorDark = Color(0xFFCF6679)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    error = ErrorLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    error = ErrorDark
)

@Composable
fun SnapShoppingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
