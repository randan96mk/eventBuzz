package app.eventbuzz.domain.repository

import app.eventbuzz.domain.model.Category
import app.eventbuzz.domain.model.Event
import app.eventbuzz.domain.model.EventFilter
import app.eventbuzz.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    fun getNearbyEvents(location: Location, filter: EventFilter): Flow<List<Event>>

    fun getEventBubbles(location: Location, filter: EventFilter): Flow<List<Event>>

    suspend fun getEventById(id: String): Event

    fun searchEvents(query: String, filter: EventFilter): Flow<List<Event>>

    fun getCategories(): Flow<List<Category>>
}
