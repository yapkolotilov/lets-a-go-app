package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.utils.toDate
import java.util.*

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
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("length")
    val length: Double,
    @SerializedName("entries")
    val entries: List<EntryDto>,
    @SerializedName("id")
    val id: Int
)

fun Route.toRouteDetailsDto() = RouteDetailsDto(
    name = name,
    difficulty = difficulty,
    points = points.map { it.toPointDto() },
    type = type,
    ground = ground,
    duration = duration().toDate(),
    entries = entries.map { it.toEntryDto() },
    length = distance(),
    id = id
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