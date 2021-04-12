package me.kolotilov.lets_a_go.models

data class RoutePoint(
    val type: Route.Type?,
    val startPoint: Point,
    val id: Int
)