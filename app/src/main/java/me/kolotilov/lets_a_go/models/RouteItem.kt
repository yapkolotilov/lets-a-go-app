package me.kolotilov.lets_a_go.models

import me.kolotilov.lets_a_go.network.output.RouteItemDto
import me.kolotilov.lets_a_go.utils.toDuration
import org.joda.time.Duration

data class RouteItem(
    val name: String?,
    val type: Route.Type?,
    val ground: Route.Ground?,
    val distance: Double,
    val duration: Duration,
    val distanceToRoute: Double?,
    val id: Int
)

fun RouteItemDto.toRouteItem() = RouteItem(
    name = name,
    type = type,
    ground = ground,
    distance = distance,
    duration = duration.toDuration(),
    distanceToRoute = distanceToRoute,
    id = id
)