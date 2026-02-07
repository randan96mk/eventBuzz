package app.eventbuzz.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    MAP(
        route = "map",
        icon = Icons.Default.Map,
        label = "Map",
    ),
    LIST(
        route = "list",
        icon = Icons.Default.List,
        label = "Events",
    ),
    SEARCH(
        route = "search",
        icon = Icons.Default.Search,
        label = "Search",
    ),
    PROFILE(
        route = "profile",
        icon = Icons.Default.Person,
        label = "Profile",
    ),
}
