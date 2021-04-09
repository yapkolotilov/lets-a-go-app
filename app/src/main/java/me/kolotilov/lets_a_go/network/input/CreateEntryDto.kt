package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Entry

data class CreateEntryDto(
    @JsonProperty("points")
    val points: List<CreatePointDto>
)

fun Entry.toCreateEntryDto() = CreateEntryDto(
    points = points.map { it.toCreatePointDto() }
)