package app.eventbuzz.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.eventbuzz.domain.model.Category
import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.usecase.SearchEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String? = null,
    val distanceKm: Int = 5,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchEvents: SearchEventsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)

        // Debounce search
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300)
                performSearch()
            }
        } else {
            _uiState.value = _uiState.value.copy(results = emptyList(), isLoading = false)
        }
    }

    fun onCategorySelected(categorySlug: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = categorySlug)
        if (_uiState.value.query.length >= 2) {
            performSearch()
        }
    }

    fun onDistanceChanged(distanceKm: Int) {
        _uiState.value = _uiState.value.copy(distanceKm = distanceKm)
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.value = SearchUiState()
    }

    private fun performSearch() {
        val state = _uiState.value
        val filter = EventFilter(
            categorySlug = state.selectedCategory,
            radiusMeters = state.distanceKm * 1000,
        )

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            searchEvents(state.query, filter)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Search failed",
                    )
                }
                .collect { results ->
                    _uiState.value = _uiState.value.copy(
                        results = results,
                        isLoading = false,
                        error = null,
                    )
                }
        }
    }
}
