package app.eventbuzz.core.common

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates the distance in kilometers between two geographic coordinates
 * using the Haversine formula.
 */
fun calculateDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
): Double {
    val earthRadiusKm = 6371.0

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) *
        cos(Math.toRadians(lat2)) *
        sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadiusKm * c
}

/**
 * Formats a distance in kilometers to a human-readable string.
 */
fun formatDistance(distanceKm: Double): String {
    return when {
        distanceKm < 1.0 -> "${(distanceKm * 1000).toInt()}m"
        distanceKm < 10.0 -> "%.1f km".format(distanceKm)
        else -> "${distanceKm.toInt()} km"
    }
}
