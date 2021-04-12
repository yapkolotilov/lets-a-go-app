package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.utils.toDateTime
import java.util.*

data class PointDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("altitude")
    val altitude: Double,
    @SerializedName("timestamp")
    val timestamp: Date,
    @SerializedName("id")
    val id: Int
)

fun PointDto.toPoint() = Point(
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    timestamp = timestamp.toDateTime(),
)