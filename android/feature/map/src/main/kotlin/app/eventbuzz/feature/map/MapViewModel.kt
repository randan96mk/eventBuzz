package app.eventbuzz.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.model.Location
import app.eventbuzz.domain.usecase.GetNearbyEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearbyEvents: GetNearbyEventsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    init {
        // Default to Bangalore for MVP testing
        setUserLocation(12.9716, 77.5946)
    }

    fun setUserLocation(latitude: Double, longitude: Double) {
        val location = Location(latitude, longitude)
        _userLocation.value = location
        loadEvents(location)
    }

    fun selectEvent(event: Event?) {
        val current = _uiState.value
        if (current is MapUiState.Success) {
            _uiState.value = current.copy(selectedEvent = event)
        }
    }

    fun setMapStyle(style: MapStyle) {
        val current = _uiState.value
        if (current is MapUiState.Success) {
            _uiState.value = current.copy(mapStyle = style)
        }
    }

    private fun loadEvents(location: Location) {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            getNearbyEvents(location, EventFilter())
                .catch { e ->
                    _uiState.value = MapUiState.Error(e.message ?: "Failed to load events")
                }
                .collect { events ->
                    _uiState.value = MapUiState.Success(events = events)
                }
        }
    }

    fun retry() {
        _userLocation.value?.let { loadEvents(it) }
    }
}
