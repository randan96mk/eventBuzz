package app.eventbuzz.core.network

import app.eventbuzz.core.network.model.EventBubbleDto
import app.eventbuzz.core.network.model.EventDetailDto
import app.eventbuzz.core.network.model.EventListItemDto
import app.eventbuzz.core.network.model.PaginatedResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBuzzApi @Inject constructor(
    private val client: HttpClient,
) {
    companion object {
        const val BASE_URL = "https://api.eventbuzz.app"
    }

    suspend fun getNearbyEvents(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        page: Int = 1,
        limit: Int = 20,
    ): PaginatedResponseDto<EventListItemDto> {
        return client.get("$BASE_URL/v1/events/nearby") {
            parameter("lat", latitude)
            parameter("lng", longitude)
            parameter("radius", radiusKm)
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getEventBubbles(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        zoomLevel: Int = 14,
    ): List<EventBubbleDto> {
        return client.get("$BASE_URL/v1/events/bubbles") {
            parameter("lat", latitude)
            parameter("lng", longitude)
            parameter("radius", radiusKm)
            parameter("zoom", zoomLevel)
        }.body()
    }

    suspend fun getEventDetail(eventId: String): EventDetailDto {
        return client.get("$BASE_URL/v1/events/$eventId").body()
    }

    suspend fun searchEvents(
        query: String,
        latitude: Double? = null,
        longitude: Double? = null,
        category: String? = null,
        page: Int = 1,
        limit: Int = 20,
    ): PaginatedResponseDto<EventListItemDto> {
        return client.get("$BASE_URL/v1/events/search") {
            parameter("q", query)
            latitude?.let { parameter("lat", it) }
            longitude?.let { parameter("lng", it) }
            category?.let { parameter("category", it) }
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }
}
