package me.kolotilov.lets_a_go.models

import org.joda.time.DateTime
import org.joda.time.Duration

data class EntryDetails(
    val finished: Boolean,
    val date: DateTime,
    val duration: Duration,
    val distance: Double,
    val speed: Double,
    val altitudeDelta: Double,
    val kiloCaloriesBurnt: Int?,
    val routeId: Int?,
    val id: Int
)
