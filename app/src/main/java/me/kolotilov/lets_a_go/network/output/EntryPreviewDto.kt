package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.EntryPreview
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class EntryPreviewDto(
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("caloriesBurnt")
    val kiloCaloriesBurnt: Int?,
    @SerializedName("altitudeDelta")
    val altitudeDelta: Double,
    @SerializedName("passed")
    val passed: Boolean,
    @SerializedName("route_id")
    val routeId: Int
)

fun EntryPreviewDto.toEntryPreview() = EntryPreview(
    distance = distance,
    duration = duration.toDuration(),
    speed = speed,
    kiloCaloriesBurnt = kiloCaloriesBurnt,
    altitudeDelta = altitudeDelta,
    passed = passed,
    routeId = routeId
)