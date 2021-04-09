package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Route

data class EditRouteDto(
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("difficulty")
    val difficulty: Int?,
    @JsonProperty("type")
    val type: Route.Type?,
    @JsonProperty("ground")
    val ground: Route.Ground?
)

fun EditRouteDto.toRoute(route: Route) = route.copy(
    name = name ?: route.name,
    difficulty = difficulty ?: route.difficulty,
    type = type ?: route.type,
    ground = ground ?: route.ground,
    id = route.id
)

fun Route.toEditRouteDto() = EditRouteDto(
    name = name,
    difficulty = difficulty,
    type = type,
    ground = ground
)