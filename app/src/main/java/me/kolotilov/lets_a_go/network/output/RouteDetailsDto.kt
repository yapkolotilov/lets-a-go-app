package me.kolotilov.lets_a_go.network.output

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Route

class RouteDetailsDto(
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("difficulty")
    val difficulty: Int?,
    @JsonProperty("points")
    val points: List<PointDto>,
    @JsonProperty("type")
    val type: Route.Type?,
    @JsonProperty("ground")
    val ground: Route.Ground?,
    @JsonProperty("entries")
    val entries: List<EntryDto>,
    @JsonProperty("id")
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