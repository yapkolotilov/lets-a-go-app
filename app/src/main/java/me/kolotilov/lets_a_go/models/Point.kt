package me.kolotilov.lets_a_go.models

import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.math.*

/**
 * Точка маршрута.
 *
 * @param latitude Широта.
 * @param longitude Долгота.
 * @param timestamp Время прохождения точки.
 * @param id ID точки.
 */
data class Point(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: DateTime
) {

    infix fun distance(other: Point): Double {
        return distance(latitude, other.latitude, longitude, other.longitude)
    }
}

fun List<Point>.distance(): Double {
    var result = 0.0
    for (i in 0 until lastIndex)
        result += this[i] distance this[i + 1]
    return result
}

fun List<Point>.duration(): Duration {
    return Duration(firstOrNull()?.timestamp ?: DateTime.now(), lastOrNull()?.timestamp ?:DateTime.now())
}

fun List<Point>.durationTillNow(): Duration {
    return Duration(firstOrNull()?.timestamp ?: DateTime.now(), DateTime.now())
}

fun List<Point>.speed(): Double {
    return distance() / duration().standardHours
}

// https://stackoverflow.com/a/16794680
private fun distance(lat1: Double, lat2: Double, lon1: Double, lon2: Double): Double {
    val el1 = 0.0
    val el2 = 0.0
    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = el1 - el2
    distance = distance.pow(2.0) + height.pow(2.0)
    return sqrt(distance)
}