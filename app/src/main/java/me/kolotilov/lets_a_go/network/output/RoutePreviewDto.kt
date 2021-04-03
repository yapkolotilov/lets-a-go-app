package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route

data class RoutePreviewDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("start_point")
    val startPoint: PointDto,
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("ground")
    val ground: Route.Ground?,
    @SerializedName("length")
    val length: Double,
    @SerializedName("id")
    val id: Int,
)

fun Route.toRoutePreviewDto() = RoutePreviewDto(
    name = name,
    startPoint = points.first().toPointDto(),
    type = type,
    ground = ground,
    length = distance(),
    id = id
)

fun RoutePreviewDto.toRoute() = Route(
    name = name,
    difficulty = null,
    type = type,
    ground = ground,
    points = listOf(startPoint.toPoint()),
    entries = emptyList(),
    id = id
)