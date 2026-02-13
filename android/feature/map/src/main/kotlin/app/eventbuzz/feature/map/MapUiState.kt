package app.eventbuzz.feature.map

import app.eventbuzz.domain.model.Event

enum class MapStyle(val label: String, val url: String) {
    LIBERTY("Liberty", "https://tiles.openfreemap.org/styles/liberty"),
    BRIGHT("Bright", "https://tiles.openfreemap.org/styles/bright"),
    DARK("Dark", "https://tiles.openfreemap.org/styles/dark"),
    POSITRON("Positron", "https://tiles.openfreemap.org/styles/positron"),
}

sealed interface MapUiState {
    data object Loading : MapUiState

    data class Success(
        val events: List<Event>,
        val selectedEvent: Event? = null,
        val mapStyle: MapStyle = MapStyle.LIBERTY,
    ) : MapUiState

    data class Error(val message: String) : MapUiState
}
