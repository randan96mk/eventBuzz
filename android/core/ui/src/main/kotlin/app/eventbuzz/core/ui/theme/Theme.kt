package app.eventbuzz.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Emerald40,
    onPrimary = Color.White,
    primaryContainer = Emerald90,
    onPrimaryContainer = Emerald10,
    secondary = GreyGreen,
    onSecondary = Color.White,
    secondaryContainer = Emerald80,
    onSecondaryContainer = Emerald10,
    tertiary = Emerald50,
    onTertiary = Emerald10,
    tertiaryContainer = Emerald60,
    onTertiaryContainer = Emerald10,
    background = Color.White,
    onBackground = DarkGreen,
    surface = Color.White,
    onSurface = DarkGreen,
    surfaceVariant = Emerald95,
    onSurfaceVariant = GreyGreen,
    outline = GreyGreenLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald50,
    onPrimary = Emerald10,
    primaryContainer = Emerald30,
    onPrimaryContainer = Emerald80,
    secondary = GreyGreenLight,
    onSecondary = Emerald10,
    secondaryContainer = EmeraldDarkSurfaceHigh,
    onSecondaryContainer = Emerald80,
    tertiary = Emerald60,
    onTertiary = Emerald10,
    tertiaryContainer = Emerald40,
    onTertiaryContainer = Emerald80,
    background = EmeraldDarkBg,
    onBackground = Emerald80,
    surface = EmeraldDarkBg,
    onSurface = Emerald80,
    surfaceVariant = EmeraldDarkSurface,
    onSurfaceVariant = GreyGreenLight,
    outline = GreyGreen,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun EventBuzzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
