package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Route

data class CreateRouteDto(
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("difficulty")
    val difficulty: Int?,
    @JsonProperty("type")
    val type: Route.Type?,
    @JsonProperty("ground")
    val ground: Route.Ground?,
    @JsonProperty("points")
    val points: List<CreatePointDto>
)