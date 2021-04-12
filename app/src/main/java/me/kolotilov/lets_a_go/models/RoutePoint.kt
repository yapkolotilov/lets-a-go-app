package me.kolotilov.lets_a_go.models

import me.kolotilov.lets_a_go.network.output.PointDto

data class RoutePoint(
    val type: Route.Type?,
    val startPoint: PointDto,
    val id: Int
)