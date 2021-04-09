package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty


data class CreateRoutePreviewDto(
    @JsonProperty("points")
    val points: List<CreatePointDto>
)