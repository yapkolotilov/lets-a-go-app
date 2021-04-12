package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RoutePreview
import org.joda.time.Duration
import java.util.*

data class RoutePreviewDto(
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
    @SerializedName("type")
    val type: Route.Type,
    @SerializedName("difficulty")
    val difficulty: Int
)

fun RoutePreviewDto.toRoutePreview() = RoutePreview(
    distance = distance,
    duration = Duration(duration.time),
    speed = speed,
    kiloCaloriesBurnt = kiloCaloriesBurnt,
    altitudeDelta = altitudeDelta,
    type = type,
    difficulty = difficulty
)