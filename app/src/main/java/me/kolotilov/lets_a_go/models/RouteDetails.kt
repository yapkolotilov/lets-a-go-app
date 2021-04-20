package me.kolotilov.lets_a_go.models

import org.joda.time.Duration

data class RouteDetails(
    val name: String?,
    val public: Boolean,
    val distance: Double,
    val duration: Duration,
    val altitudeDelta: Double,
    val speed: Double,
    val kilocaloriesBurnt: Int?,
    val difficulty: Int?,
    val type: Route.Type?,
    val ground: Route.Ground?,
    val entries: List<RouteEntry>,
    val mine: Boolean,
    val totalDistance: Double,
    val totalCaloriesBurnt: Int?,
    val id: Int,
)
