package app.eventbuzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.eventbuzz.core.ui.theme.EventBuzzTheme
import app.eventbuzz.navigation.EventBuzzNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
            EventBuzzTheme(darkTheme = isDarkMode) {
                EventBuzzNavHost(
                    isDarkMode = isDarkMode,
                    onDarkModeChanged = { themeViewModel.setDarkMode(it) },
                )
            }
        }
    }
}
