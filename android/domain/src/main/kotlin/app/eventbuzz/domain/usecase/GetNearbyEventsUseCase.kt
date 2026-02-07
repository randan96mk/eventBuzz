package app.eventbuzz.domain.usecase

import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.model.Location
import app.eventbuzz.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetNearbyEventsUseCase(
    private val repository: EventRepository,
) {
    operator fun invoke(location: Location, filter: EventFilter = EventFilter()): Flow<List<Event>> {
        return repository.getNearbyEvents(location, filter)
    }
}
