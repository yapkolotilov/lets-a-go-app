package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route

data class EditRouteDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("difficulty")
    val difficulty: Int?,
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("ground")
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