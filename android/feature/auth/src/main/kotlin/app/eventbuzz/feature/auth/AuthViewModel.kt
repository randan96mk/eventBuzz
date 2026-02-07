package app.eventbuzz.feature.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun continueAsGuest(onSuccess: () -> Unit) {
        // Guest mode: skip auth, navigate to main
        onSuccess()
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        // TODO: Implement Keycloak OIDC auth flow
        _uiState.value = _uiState.value.copy(isLoading = true)
        onSuccess()
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        // TODO: Implement Keycloak registration
        _uiState.value = _uiState.value.copy(isLoading = true)
        onSuccess()
    }
}
