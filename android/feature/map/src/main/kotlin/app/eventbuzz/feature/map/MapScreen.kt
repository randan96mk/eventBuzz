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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MapScreen(
    onEventClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    var isMapExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ),
    ) {
        when (val state = uiState) {
            is MapUiState.Loading -> LoadingIndicator()

            is MapUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
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
                val selectedEvent = state.selectedEvent
                val currentStyle = state.mapStyle

                // Map section — expands to full screen on tap, collapses back
                val mapModifier = if (isMapExpanded) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                }

                Box(modifier = mapModifier) {
                    var mapBoxSize by remember { mutableStateOf(IntSize.Zero) }
                    val density = LocalDensity.current

                    MapLibreView(
                        events = state.events,
                        mapStyle = state.mapStyle,
                        centerLat = userLocation?.latitude ?: 12.9716,
                        centerLng = userLocation?.longitude ?: 77.5946,
                        onMarkerClick = { event -> viewModel.selectEvent(event) },
                        onMapTap = {
                            if (selectedEvent != null) {
                                viewModel.selectEvent(null)
                            } else {
                                isMapExpanded = !isMapExpanded
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { mapBoxSize = it.size },
                    )

                    // Map style chips overlay
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
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    enabled = true,
                                    selected = currentStyle == style,
                                ),
                            )
                        }
                    }

                    // Collapse button — shown only in expanded mode
                    if (isMapExpanded) {
                        IconButton(
                            onClick = { isMapExpanded = false },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .zIndex(3f)
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    shape = CircleShape,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Collapse map",
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    // Event popup card overlay
                    var popupSize by remember { mutableStateOf(IntSize.Zero) }

                    AnimatedVisibility(
                        visible = selectedEvent != null,
                        enter = fadeIn() + scaleIn(initialScale = 0.8f),
                        exit = fadeOut() + scaleOut(targetScale = 0.8f),
                        modifier = Modifier
                            .zIndex(2f)
                            .onGloballyPositioned { popupSize = it.size }
                            .offset {
                                val markerIconPx = 48
                                val gapPx = with(density) { 8.dp.roundToPx() }
                                val x = (mapBoxSize.width - popupSize.width) / 2
                                val y = mapBoxSize.height / 2 - markerIconPx / 2 - gapPx - popupSize.height
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

                // Nearby Events section — hidden when map is expanded
                if (!isMapExpanded) {
                    NearbyEventsSection(
                        events = state.events,
                        sortMode = state.sortMode,
                        onSortModeChange = { viewModel.setSortMode(it) },
                        onEventClick = onEventClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun NearbyEventsSection(
    events: List<Event>,
    sortMode: SortMode,
    onSortModeChange: (SortMode) -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d, yyyy '\u2022' h:mm a", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Section header
        Text(
            text = "Nearby Events",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 4.dp),
        )

        // Sort chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            items(SortMode.entries.size) { index ->
                val mode = SortMode.entries[index]
                FilterChip(
                    selected = sortMode == mode,
                    onClick = { onSortModeChange(mode) },
                    label = {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline,
                        enabled = true,
                        selected = sortMode == mode,
                    ),
                )
            }
        }

        // Event cards list
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(events, key = { it.id }) { event ->
                NearbyEventCard(
                    event = event,
                    dateFormatter = dateFormatter,
                    onClick = { onEventClick(event.id) },
                )
            }
        }
    }
}

@Composable
private fun NearbyEventCard(
    event: Event,
    dateFormatter: DateTimeFormatter,
    onClick: () -> Unit,
) {
    val categoryColor = remember(event.category.colorHex) {
        androidx.compose.ui.graphics.Color(Color.parseColor(event.category.colorHex))
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Event image
            if (event.imageUrl != null) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            } else {
                // Colored placeholder based on category
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = event.category.name.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                // Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Date
                Text(
                    text = dateFormatter.format(event.startDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Category chip + distance
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Category chip with colored background
                    Text(
                        text = event.category.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier
                            .background(
                                color = categoryColor,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                    )

                    event.distanceMeters?.let { dist ->
                        val distText = if (dist < 1000) {
                            "${dist.toInt()} m"
                        } else {
                            "%.1f km".format(dist / 1000)
                        }
                        Text(
                            text = distText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        modifier = Modifier.widthIn(max = 300.dp),
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

            val iconFactory = IconFactory.getInstance(context)
            val iconCache = mutableMapOf<String, org.maplibre.android.annotations.Icon>()

            val eventByTitle = mutableMapOf<String, Event>()
            events.forEach { event ->
                val colorHex = event.category.colorHex
                val letter = event.category.name.first().uppercase()
                val icon = iconCache.getOrPut(colorHex) {
                    iconFactory.fromBitmap(createMarkerBitmap(colorHex, letter))
                }

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

private fun createMarkerBitmap(colorHex: String, letter: String): Bitmap {
    val size = 48
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw filled circle with category color
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(colorHex)
        style = Paint.Style.FILL
        setShadowLayer(4f, 0f, 2f, Color.argb(80, 0, 0, 0))
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, circlePaint)

    // Draw white border
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, borderPaint)

    // Draw category initial letter
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    val textY = size / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
    canvas.drawText(letter, size / 2f, textY, textPaint)

    return bitmap
}
