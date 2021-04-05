package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route

class RouteDetailsDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("difficulty")
    val difficulty: Int?,
    @SerializedName("points")
    val points: List<PointDto>,
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("ground")
    val ground: Route.Ground?,
    @SerializedName("entries")
    val entries: List<EntryDto>,
    @SerializedName("id")
    val id: Int
)

fun RouteDetailsDto.toRoute() = Route(
    name = name,
    difficulty = difficulty,
    points = points.map { it.toPoint() },
    type = type,
    ground = ground,
    entries = entries.map { it.toEntry() },
    id = id
)