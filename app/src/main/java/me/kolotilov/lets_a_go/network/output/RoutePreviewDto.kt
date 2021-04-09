package me.kolotilov.lets_a_go.network.output

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RoutePreview
import org.joda.time.Duration
import java.util.*

data class RoutePreviewDto(
    @JsonProperty("distance")
    val distance: Double,
    @JsonProperty("duration")
    val duration: Date,
    @JsonProperty("speed")
    val speed: Double,
    @JsonProperty("caloriesBurnt")
    val kiloCaloriesBurnt: Int?,
    @JsonProperty("altitudeDelta")
    val altitudeDelta: Double,
    @JsonProperty("type")
    val type: Route.Type,
    @JsonProperty("difficulty")
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