package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val NeonGamingColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = DarkCanvas,
  primaryContainer = DarkCardElevated,
  onPrimaryContainer = NeonCyan,
  secondary = NeonViolet,
  onSecondary = Color.White,
  secondaryContainer = DarkCardElevated,
  onSecondaryContainer = NeonViolet,
  tertiary = ElectricBlue,
  onTertiary = Color.White,
  background = DarkCanvas,
  onBackground = TextPrimary,
  surface = DarkCard,
  onSurface = TextPrimary,
  surfaceVariant = DarkCardElevated,
  onSurfaceVariant = TextSecondary,
  outline = DarkCardBorder,
  error = NeonRed,
  onError = Color.White
)

@Composable
fun ZXGameBoosterTheme(
  dynamicColor: Boolean = false, // Preserve Neon Brutalism gaming branding
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      dynamicDarkColorScheme(context)
    }
    else -> NeonGamingColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Backwards compatibility alias for template references
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  ZXGameBoosterTheme(dynamicColor = dynamicColor, content = content)
}

