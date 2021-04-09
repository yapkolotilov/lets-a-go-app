package me.kolotilov.lets_a_go.network.output

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.utils.toDateTime
import java.util.*

data class PointDto(
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("altitude")
    val altitude: Double,
    @JsonProperty("timestamp")
    val timestamp: Date,
    @JsonProperty("id")
    val id: Int
)

fun PointDto.toPoint() = Point(
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    timestamp = timestamp.toDateTime(),
)