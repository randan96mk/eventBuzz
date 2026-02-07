package app.eventbuzz.domain.model

import java.time.Instant

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val category: Category,
    val location: Location,
    val address: String?,
    val city: String?,
    val startDate: Instant,
    val endDate: Instant?,
    val imageUrl: String?,
    val ticketUrl: String?,
    val priceMin: Double?,
    val priceMax: Double?,
    val currency: String,
    val tags: List<String>,
    val images: List<String>,
    val distanceMeters: Double?,
)
