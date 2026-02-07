package app.eventbuzz.domain.model

data class Category(
    val id: Int,
    val name: String,
    val slug: String,
    val colorHex: String,
    val iconName: String,
)
