package app.eventbuzz.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventBubbleDto(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    @SerialName("attendee_count")
    val attendeeCount: Int,
    @SerialName("image_url")
    val imageUrl: String? = null,
)

@Serializable
data class EventListItemDto(
    val id: String,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: CategoryDto,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("attendee_count")
    val attendeeCount: Int = 0,
    @SerialName("venue_name")
    val venueName: String? = null,
)

@Serializable
data class EventDetailDto(
    val id: String,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: CategoryDto,
    @SerialName("start_time")
    val startTime: String,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("attendee_count")
    val attendeeCount: Int = 0,
    @SerialName("venue_name")
    val venueName: String? = null,
    @SerialName("venue_address")
    val venueAddress: String? = null,
    @SerialName("organizer_name")
    val organizerName: String? = null,
    @SerialName("organizer_image_url")
    val organizerImageUrl: String? = null,
    val price: String? = null,
    val tags: List<String> = emptyList(),
    @SerialName("is_attending")
    val isAttending: Boolean = false,
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val slug: String,
    val color: String? = null,
)

@Serializable
data class PaginatedResponseDto<T>(
    val data: List<T>,
    val page: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_count")
    val totalCount: Int,
)
