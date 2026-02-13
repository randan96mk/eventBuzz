package app.eventbuzz.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.model.Location
import app.eventbuzz.domain.model.SortOption
import app.eventbuzz.domain.usecase.GetNearbyEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val sortBy: SortOption = SortOption.DISTANCE,
)

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getNearbyEvents: GetNearbyEventsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private var currentLocation: Location = Location(40.7128, -74.0060) // Default NYC

    init {
        loadEvents()
    }

    fun setLocation(latitude: Double, longitude: Double) {
        currentLocation = Location(latitude, longitude)
        loadEvents()
    }

    fun setSortOption(sortBy: SortOption) {
        _uiState.value = _uiState.value.copy(sortBy = sortBy)
        loadEvents()
    }

    fun refresh() {
        loadEvents()
    }

    private fun loadEvents() {
        val filter = EventFilter(sortBy = _uiState.value.sortBy)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getNearbyEvents(currentLocation, filter)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load events",
                    )
                }
                .collect { events ->
                    _uiState.value = _uiState.value.copy(
                        events = events,
                        isLoading = false,
                        error = null,
                    )
                }
        }
    }
}
