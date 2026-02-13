package app.eventbuzz.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.eventbuzz.core.ui.components.EventCard
import app.eventbuzz.core.ui.components.EventCardData
import app.eventbuzz.core.ui.components.LoadingIndicator

private val categories = listOf(
    "music" to "Music",
    "sports" to "Sports",
    "food-drink" to "Food & Drink",
    "arts" to "Arts",
    "tech" to "Tech",
    "outdoor" to "Outdoor",
    "community" to "Community",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onEventClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = uiState.query,
                    onQueryChange = { viewModel.onQueryChange(it) },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search events...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {}

        // Category filter chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            categories.forEach { (slug, name) ->
                FilterChip(
                    selected = uiState.selectedCategory == slug,
                    onClick = {
                        viewModel.onCategorySelected(
                            if (uiState.selectedCategory == slug) null else slug,
                        )
                    },
                    label = { Text(name) },
                )
            }
        }

        // Distance slider
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Distance: ${uiState.distanceKm} km",
                style = MaterialTheme.typography.labelMedium,
            )
            Slider(
                value = uiState.distanceKm.toFloat(),
                onValueChange = { viewModel.onDistanceChanged(it.toInt()) },
                valueRange = 1f..50f,
                steps = 49,
            )
        }

        // Results
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> {
                Text(
                    text = uiState.error.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.results, key = { it.id }) { event ->
                        EventCard(
                            event = EventCardData(
                                id = event.id,
                                title = event.title,
                                imageUrl = event.imageUrl,
                                date = event.startDate.toString(),
                                distance = event.distanceMeters?.let { "${(it / 1000).toInt()} km" },
                                category = event.category.name,
                            ),
                            onClick = { onEventClick(event.id) },
                        )
                    }
                }
            }
        }
    }
}
