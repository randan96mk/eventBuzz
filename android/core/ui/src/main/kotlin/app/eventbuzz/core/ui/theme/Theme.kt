package app.eventbuzz.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Amber40,
    onPrimary = Color.White,
    primaryContainer = Amber90,
    onPrimaryContainer = Amber10,
    secondary = WarmGrey,
    onSecondary = Color.White,
    secondaryContainer = Amber80,
    onSecondaryContainer = Amber10,
    tertiary = Amber50,
    onTertiary = Color.White,
    tertiaryContainer = Amber60,
    onTertiaryContainer = Amber10,
    background = Amber99,
    onBackground = WarmBrown,
    surface = Amber99,
    onSurface = WarmBrown,
    surfaceVariant = Amber95,
    onSurfaceVariant = WarmGrey,
    outline = WarmGreyLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Amber60,
    onPrimary = Amber10,
    primaryContainer = Amber30,
    onPrimaryContainer = Amber90,
    secondary = WarmGreyLight,
    onSecondary = Amber20,
    secondaryContainer = AmberDarkSurfaceHigh,
    onSecondaryContainer = Amber90,
    tertiary = Amber70,
    onTertiary = Amber10,
    tertiaryContainer = Amber40,
    onTertiaryContainer = Amber90,
    background = AmberDarkBg,
    onBackground = Amber90,
    surface = AmberDarkBg,
    onSurface = Amber90,
    surfaceVariant = AmberDarkSurface,
    onSurfaceVariant = WarmGreyLight,
    outline = WarmGrey,
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
