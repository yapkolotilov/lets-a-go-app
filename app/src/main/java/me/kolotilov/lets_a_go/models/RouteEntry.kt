package me.kolotilov.lets_a_go.models

import org.joda.time.DateTime
import org.joda.time.Duration

data class RouteEntry(
    val date: DateTime,
    val duration: Duration,
    val passed: Boolean,
    val routeId: Int,
    val id: Int
)
