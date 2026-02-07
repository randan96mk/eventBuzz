package app.eventbuzz.feature.map

import app.eventbuzz.domain.model.Event

sealed interface MapUiState {
    data object Loading : MapUiState

    data class Success(
        val events: List<Event>,
        val selectedEvent: Event? = null,
    ) : MapUiState

    data class Error(val message: String) : MapUiState
}
