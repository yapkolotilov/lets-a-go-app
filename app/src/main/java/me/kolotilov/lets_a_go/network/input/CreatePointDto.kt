package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Point
import java.util.*

data class CreatePointDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timestamp")
    val timestamp: Date
)

fun Point.toCreatePointDto() = CreatePointDto(
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp.toDate()
)