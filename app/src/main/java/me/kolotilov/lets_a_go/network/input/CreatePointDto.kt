package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Point
import java.util.*

data class CreatePointDto(
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("timestamp")
    val timestamp: Date
)

fun Point.toCreatePointDto() = CreatePointDto(
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp.toDate()
)