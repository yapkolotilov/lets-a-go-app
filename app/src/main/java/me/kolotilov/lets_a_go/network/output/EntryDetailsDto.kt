package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.EntryDetails
import me.kolotilov.lets_a_go.utils.toDateTime
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class EntryDetailsDto(
    @SerializedName("finished")
    val finished: Boolean,
    @SerializedName("date")
    val date: Date,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("altitude_delta")
    val altitudeDelta: Double,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("kilocalories_burnt")
    val kiloCaloriesBurnt: Int?,
    @SerializedName("route_id")
    val routeId: Int?,
    @SerializedName("id")
    val id: Int
)

fun EntryDetailsDto.toEntryDetails() = EntryDetails(
    finished = finished,
    date = date.toDateTime(),
    duration = duration.toDuration(),
    distance = distance,
    speed = speed,
    altitudeDelta = altitudeDelta,
    kiloCaloriesBurnt = kiloCaloriesBurnt,
    routeId = routeId,
    id = id,
)