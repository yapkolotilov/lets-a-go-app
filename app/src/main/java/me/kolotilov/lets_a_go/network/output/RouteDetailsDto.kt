 package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RouteDetails
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

class RouteDetailsDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("public")
    val public: Boolean,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("altitude_delta")
    val altitudeDelta: Double,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("kilocalories_burnt")
    val kilocaloriesBurnt: Int?,
    @SerializedName("difficulty")
    val difficulty: Int?,
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("ground")
    val ground: Route.Ground?,
    @SerializedName("entries")
    val entries: List<RouteEntryDto>,
    @SerializedName("mine")
    val mine: Boolean,
    @SerializedName("total_distance")
    val totalDistance: Double,
    @SerializedName("total_calories_burnt")
    val totalCaloriesBurnt: Int?,
    @SerializedName("id")
    val id: Int,
)

fun RouteDetailsDto.toRouteDetails() = RouteDetails(
    name = name,
    public = public,
    distance = distance,
    duration = duration.toDuration(),
    altitudeDelta = altitudeDelta,
    speed = speed,
    kilocaloriesBurnt = kilocaloriesBurnt,
    difficulty = difficulty?.takeIf { it > 0 },
    type = type,
    ground = ground,
    entries = entries.map { it.toRouteEntry() },
    mine = mine,
    totalDistance = totalDistance,
    totalCaloriesBurnt = totalCaloriesBurnt,
    id = id
)