package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Entry

data class EntryDto(
    @SerializedName("points")
    val points: List<PointDto>,
    @SerializedName("id")
    val id: Int
)

fun EntryDto.toEntry() = Entry(
    points = points.map { it.toPoint() },
    id = id
)