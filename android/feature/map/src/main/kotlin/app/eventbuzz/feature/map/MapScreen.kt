package app.eventbuzz.feature.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.eventbuzz.core.ui.components.LoadingIndicator
import app.eventbuzz.domain.model.Event
import coil3.compose.AsyncImage
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap

@Composable
fun MapScreen(
    onEventClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val selectedEvent = (uiState as? MapUiState.Success)?.selectedEvent
    val currentStyle = (uiState as? MapUiState.Success)?.mapStyle ?: MapStyle.LIBERTY

    Box(modifier = Modifier.fillMaxSize()) {
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
                // Track the Box size so we can position popup above center
                // (animateCamera always centers the tapped marker on screen)
                var boxSize by remember { mutableStateOf(IntSize.Zero) }
                val density = LocalDensity.current

                // Map view (full screen, behind overlays)
                MapLibreView(
                    events = state.events,
                    mapStyle = state.mapStyle,
                    centerLat = userLocation?.latitude ?: 12.9716,
                    centerLng = userLocation?.longitude ?: 77.5946,
                    onMarkerClick = { event ->
                        viewModel.selectEvent(event)
                    },
                    onMapTap = { viewModel.selectEvent(null) },
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { boxSize = it.size },
                )

                // Map style selector overlay at top
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

                // Event popup card overlay — positioned above the tapped marker
                var popupSize by remember { mutableStateOf(IntSize.Zero) }

                AnimatedVisibility(
                    visible = selectedEvent != null,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f),
                    modifier = Modifier
                        .zIndex(2f)
                        .onGloballyPositioned { popupSize = it.size }
                        .offset {
                            // animateCamera centers the tapped marker on screen,
                            // so the marker lands at (boxCenter.x, boxCenter.y).
                            // Position popup so its pointer tip sits just above the marker.
                            val markerIconPx = 48 // marker bitmap size in px
                            val gapPx = with(density) { 8.dp.roundToPx() }

                            // Center popup horizontally
                            val x = (boxSize.width - popupSize.width) / 2
                            // Place popup above the marker center
                            val y = boxSize.height / 2 - markerIconPx / 2 - gapPx - popupSize.height

                            IntOffset(x.coerceAtLeast(0), y.coerceAtLeast(0))
                        },
                ) {
                    selectedEvent?.let { event ->
                        EventPopupCard(
                            event = event,
                            onTap = { onEventClick(event.id) },
                            onDismiss = { viewModel.selectEvent(null) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventPopupCard(
    event: Event,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Card body
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onTap),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceColor,
            ),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Header row: title + close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        event.address?.let { addr ->
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = addr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Image + details row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.imageUrl != null) {
                        AsyncImage(
                            model = event.imageUrl,
                            contentDescription = event.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(MaterialTheme.shapes.medium),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = event.category.name,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                },
                            )
                            event.distanceMeters?.let { dist ->
                                Text(
                                    text = "${(dist / 1000).toInt()} km",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = event.startDate.toString().substringBefore("T"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap for details",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // Pointer triangle pointing down toward the marker
        Spacer(
            modifier = Modifier
                .size(width = 24.dp, height = 12.dp)
                .drawBehind {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width / 2f, size.height)
                        close()
                    }
                    drawPath(path, color = surfaceColor)
                },
        )
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
        color = Color.parseColor("#00C853")
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
