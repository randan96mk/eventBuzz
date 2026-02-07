package app.eventbuzz.domain.usecase

import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.repository.EventRepository

class GetEventDetailUseCase(
    private val repository: EventRepository,
) {
    suspend operator fun invoke(eventId: String): Event {
        return repository.getEventById(eventId)
    }
}
