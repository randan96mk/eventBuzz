package app.eventbuzz.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.usecase.GetEventDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val event: Event) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEventDetail: GetEventDetailUseCase,
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadEvent()
    }

    fun retry() {
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val event = getEventDetail(eventId)
                _uiState.value = DetailUiState.Success(event)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Failed to load event")
            }
        }
    }
}
