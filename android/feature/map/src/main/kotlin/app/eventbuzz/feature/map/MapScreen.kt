package app.eventbuzz.feature.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.eventbuzz.core.ui.components.EventCard
import app.eventbuzz.core.ui.components.EventCardData
import app.eventbuzz.core.ui.components.LoadingIndicator
import app.eventbuzz.domain.model.Event
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onEventClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val selectedEvent = (uiState as? MapUiState.Success)?.selectedEvent
    val currentStyle = (uiState as? MapUiState.Success)?.mapStyle ?: MapStyle.LIBERTY

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    LaunchedEffect(selectedEvent) {
        if (selectedEvent != null) {
            scaffoldState.bottomSheetState.partialExpand()
        } else {
            scaffoldState.bottomSheetState.hide()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (selectedEvent != null) 200.dp else 0.dp,
        sheetContent = {
            selectedEvent?.let { event ->
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
                    // Map style selector overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .zIndex(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        MapStyle.entries.forEach { style ->
                            FilterChip(
                                selected = currentStyle == style,
                                onClick = { viewModel.setMapStyle(style) },
                                label = {
                                    Text(
                                        text = style.label,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    enabled = true,
                                    selected = currentStyle == style,
                                ),
                            )
                        }
                    }

                    // Map view
                    MapLibreView(
                        events = state.events,
                        mapStyle = state.mapStyle,
                        centerLat = userLocation?.latitude ?: 40.7128,
                        centerLng = userLocation?.longitude ?: -74.0060,
                        onMarkerClick = { event -> viewModel.selectEvent(event) },
                        onMapTap = { viewModel.selectEvent(null) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun MapLibreView(
    events: List<Event>,
    mapStyle: MapStyle,
    centerLat: Double,
    centerLng: Double,
    onMarkerClick: (Event) -> Unit,
    onMapTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initialize MapLibre once
    remember { MapLibre.getInstance(context) }

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { map ->
                map.setStyle(mapStyle.url)
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(centerLat, centerLng))
                    .zoom(11.0)
                    .build()
            }
        }
    }

    // Track the current map reference for style changes
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }

    // Store the map reference
    LaunchedEffect(Unit) {
        mapView.getMapAsync { map -> mapRef = map }
    }

    // Update style when it changes
    LaunchedEffect(mapStyle) {
        mapRef?.setStyle(mapStyle.url)
    }

    // Add markers when events change
    LaunchedEffect(events) {
        mapView.getMapAsync { map ->
            map.markers.forEach { map.removeMarker(it) }

            val markerIcon = createMarkerBitmap()
            val icon = IconFactory.getInstance(context).fromBitmap(markerIcon)

            val eventByTitle = mutableMapOf<String, Event>()
            events.forEach { event ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(event.location.latitude, event.location.longitude))
                        .title(event.title)
                        .snippet(event.category.name)
                        .icon(icon)
                )
                eventByTitle[event.title] = event
            }

            map.setOnMarkerClickListener { marker ->
                val event = eventByTitle[marker.title]
                if (event != null) {
                    onMarkerClick(event)
                    map.animateCamera(
                        org.maplibre.android.camera.CameraUpdateFactory.newLatLngZoom(
                            LatLng(event.location.latitude, event.location.longitude),
                            14.0,
                        )
                    )
                }
                true
            }

            map.addOnMapClickListener {
                onMapTap()
                false
            }
        }
    }

    // Handle lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

private fun createMarkerBitmap(): Bitmap {
    val size = 48
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw circle
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6750A4")
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, circlePaint)

    // Draw border
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, borderPaint)

    // Draw "E" letter
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    canvas.drawText("E", size / 2f, size / 2f + 8f, textPaint)

    return bitmap
}
