package app.eventbuzz.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Guest user info
        Text(
            text = "Guest User",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onNavigateToAuth,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Create Account")
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        // Settings
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        // Default distance
        ListItem(
            headlineContent = { Text("Default distance") },
            supportingContent = { Text("5 km") },
        )

        // Dark mode
        var darkMode by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text("Dark mode") },
            trailingContent = {
                Switch(
                    checked = darkMode,
                    onCheckedChange = { darkMode = it },
                )
            },
        )

        // Notifications
        var notifications by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text("Notifications") },
            trailingContent = {
                Switch(
                    checked = notifications,
                    onCheckedChange = { notifications = it },
                )
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        // About section
        ListItem(headlineContent = { Text("About") })
        ListItem(headlineContent = { Text("Privacy Policy") })
        ListItem(headlineContent = { Text("Open-Source Licenses") })

        Text(
            text = "EventBuzz v1.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
