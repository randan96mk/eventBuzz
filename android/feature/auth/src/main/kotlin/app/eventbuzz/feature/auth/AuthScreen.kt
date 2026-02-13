package app.eventbuzz.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Logo / title
        Text(
            text = "EventBuzz",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Discover events around you",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Guest button
        Button(
            onClick = { viewModel.continueAsGuest(onAuthSuccess) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continue as Guest")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign up
        OutlinedButton(
            onClick = { /* TODO: navigate to sign-up form */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign Up with Email")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sign in
        OutlinedButton(
            onClick = { /* TODO: navigate to sign-in form */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        // Social login (Phase 2)
        Text(
            text = "Social login coming in Phase 2",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}
