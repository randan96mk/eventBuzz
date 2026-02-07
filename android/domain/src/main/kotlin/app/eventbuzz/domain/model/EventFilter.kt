package app.eventbuzz.domain.model

import java.time.Instant

data class EventFilter(
    val categorySlug: String? = null,
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
    val radiusMeters: Int = 5000,
    val query: String? = null,
    val sortBy: SortOption = SortOption.DISTANCE,
)

enum class SortOption {
    DISTANCE,
    DATE,
    POPULAR,
}
