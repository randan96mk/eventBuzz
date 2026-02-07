package app.eventbuzz.domain.usecase

import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class SearchEventsUseCase(
    private val repository: EventRepository,
) {
    operator fun invoke(query: String, filter: EventFilter = EventFilter()): Flow<List<Event>> {
        return repository.searchEvents(query, filter)
    }
}
