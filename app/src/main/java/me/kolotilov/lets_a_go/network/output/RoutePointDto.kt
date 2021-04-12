package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.models.RoutePoint

data class RoutePointDto(
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("start_point")
    val startPoint: PointDto,
    @SerializedName("id")
    val id: Int
)

fun RoutePointDto.toRoutePoint() = RoutePoint(
    type = type,
    startPoint = startPoint,
    id = id
)