package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.StartEntry

data class StartEntryDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("points")
    val points: List<PointDto>,
)

fun StartEntryDto.toStartEntry() = StartEntry(
    id = id,
    name = name,
    points = points.map { it.toPoint() }
)
