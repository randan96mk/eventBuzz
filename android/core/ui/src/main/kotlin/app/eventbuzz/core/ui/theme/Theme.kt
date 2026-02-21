package app.eventbuzz.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Forest Green + Off-White — Light palette
private val LightColorScheme = lightColorScheme(
    primary = Forest40,                         // #228B22 Forest Green
    onPrimary = Color.White,
    primaryContainer = Forest90,                // #E6F2E6
    onPrimaryContainer = Forest10,              // #0D3D0D
    secondary = ForestOnSurfaceVariant,         // #4A5C4A
    onSecondary = Color.White,
    secondaryContainer = Forest80,              // #A5D6A5
    onSecondaryContainer = Forest10,            // #0D3D0D
    tertiary = Forest50,                        // #2EA62E
    onTertiary = Forest10,
    tertiaryContainer = Forest70,               // #66BB6A
    onTertiaryContainer = Forest10,
    background = Forest99,                      // #F9F9F9 Off-White
    onBackground = ForestOnSurface,             // #1A1C1A
    surface = Color.White,                      // #FFFFFF
    onSurface = ForestOnSurface,                // #1A1C1A
    surfaceVariant = Forest95,                  // #F0F4F0
    onSurfaceVariant = ForestOnSurfaceVariant,  // #4A5C4A
    outline = ForestOutline,                    // #8A9E8A
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

// Forest Green + Off-White — Dark palette
private val DarkColorScheme = darkColorScheme(
    primary = Forest60,                         // #4CAF50 lighter for dark-mode contrast
    onPrimary = ForestDarkBg,                   // #0D1A0D
    primaryContainer = Forest30,                // #1A6B1A
    onPrimaryContainer = Forest80,              // #A5D6A5
    secondary = ForestOutline,                  // #8A9E8A
    onSecondary = Forest10,
    secondaryContainer = ForestDarkSurfaceHigh, // #243824
    onSecondaryContainer = Forest80,            // #A5D6A5
    tertiary = Forest70,                        // #66BB6A
    onTertiary = Forest10,
    tertiaryContainer = Forest40,               // #228B22
    onTertiaryContainer = Forest80,             // #A5D6A5
    background = ForestDarkBg,                  // #0D1A0D Deep Forest
    onBackground = ForestDarkOnSurface,         // #D5E8D5
    surface = ForestDarkBg,                     // #0D1A0D
    onSurface = ForestDarkOnSurface,            // #D5E8D5
    surfaceVariant = ForestDarkSurface,         // #1A2E1A Dark Leaf
    onSurfaceVariant = ForestOutline,           // #8A9E8A
    outline = ForestOnSurfaceVariant,           // #4A5C4A
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
