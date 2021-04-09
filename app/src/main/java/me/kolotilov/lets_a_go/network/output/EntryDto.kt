package me.kolotilov.lets_a_go.network.output

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Entry

data class EntryDto(
    @JsonProperty("points")
    val points: List<PointDto>,
    @JsonProperty("id")
    val id: Int
)

fun EntryDto.toEntry() = Entry(
    points = points.map { it.toPoint() },
    id = id
)