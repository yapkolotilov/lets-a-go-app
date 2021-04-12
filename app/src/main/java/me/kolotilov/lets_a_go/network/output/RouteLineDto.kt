package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RouteLine

data class RouteLineDto(
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("points")
    val points: List<PointDto>,
    @SerializedName("id")
    val id: Int,
)

fun RouteLineDto.toRouteLine() = RouteLine(
    type = type,
    points = points.map { it.toPoint() },
    id = id,
)