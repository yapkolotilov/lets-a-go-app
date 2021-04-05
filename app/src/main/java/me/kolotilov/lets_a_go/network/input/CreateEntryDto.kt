package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Entry

data class CreateEntryDto(
    @SerializedName("points")
    val points: List<CreatePointDto>
)

fun Entry.toCreateEntryDto() = CreateEntryDto(
    points = points.map { it.toCreatePointDto() }
)