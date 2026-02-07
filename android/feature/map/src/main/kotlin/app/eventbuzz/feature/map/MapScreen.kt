package app.eventbuzz.feature.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.eventbuzz.core.ui.components.EventCard
import app.eventbuzz.core.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onEventClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedEvent = (uiState as? MapUiState.Success)?.selectedEvent

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = if (selectedEvent != null) SheetValue.PartiallyExpanded else SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (selectedEvent != null) 200.dp else 0.dp,
        sheetContent = {
            selectedEvent?.let { event ->
                EventCard(
                    title = event.title,
                    dateTime = event.startDate.toString(),
                    category = event.category.name,
                    categoryColor = event.category.colorHex,
                    distance = event.distanceMeters?.let { "${(it / 1000).toInt()} km" },
                    imageUrl = event.imageUrl,
                    onClick = { onEventClick(event.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is MapUiState.Loading -> LoadingIndicator()

                is MapUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Button(
                            onClick = { viewModel.retry() },
                            modifier = Modifier.padding(top = 16.dp),
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is MapUiState.Success -> {
                    // TODO: Replace with MapLibre MapView integration
                    // MapLibre composable will render here with event markers
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Map View — ${state.events.size} events loaded",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                }
            }
        }
    }
}
