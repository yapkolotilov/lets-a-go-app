package me.kolotilov.lets_a_go.models

data class RouteLine(
    val type: Route.Type?,
    val points: List<Point>,
    val id: Int,
)
