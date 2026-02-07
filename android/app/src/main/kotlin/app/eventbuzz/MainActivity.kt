package app.eventbuzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.eventbuzz.core.ui.theme.EventBuzzTheme
import app.eventbuzz.navigation.EventBuzzNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventBuzzTheme {
                EventBuzzNavHost()
            }
        }
    }
}
