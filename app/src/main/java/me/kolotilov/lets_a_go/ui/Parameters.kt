package me.kolotilov.lets_a_go.ui

import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RoutePreview
import me.kolotilov.lets_a_go.utils.toDate
import me.kolotilov.lets_a_go.utils.toDateTime
import me.kolotilov.lets_a_go.utils.toDuration
import java.io.Serializable
import java.util.*

data class EditRouteParams(
    val distance: Double,
    val duration: Date,
    val speed: Double,
    val kiloCaloriesBurnt: Int?,
    val altitudeDelta: Double,
    val type: Route.Type,
    val difficulty: Int,
    val points: List<PointParam>
) : Serializable

fun RoutePreview.toEditRouteParams(points: List<Point>) = EditRouteParams(
    distance = distance,
    duration = duration.toDate(),
    speed = speed,
    kiloCaloriesBurnt = kiloCaloriesBurnt,
    altitudeDelta = altitudeDelta,
    type = type,
    difficulty = difficulty,
    points = points.map { it.toPointParam() }
)

fun EditRouteParams.toRoutePreview() = RoutePreview(
    distance = distance,
    duration = duration.toDuration(),
    speed = speed,
    kiloCaloriesBurnt = kiloCaloriesBurnt,
    altitudeDelta = altitudeDelta,
    type = type,
    difficulty = difficulty
)


data class PointParam(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: Date
)

fun Point.toPointParam() = PointParam(
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    timestamp = timestamp.toDate()
)

fun PointParam.toPoint() = Point(
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    timestamp = timestamp.toDateTime()
)