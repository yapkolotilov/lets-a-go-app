package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Entry
import me.kolotilov.lets_a_go.utils.toDateTime
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class CreateEntryDto(
    @SerializedName("timestamp")
    val timestamp: Date,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("finished")
    val finished: Boolean
)

fun CreateEntryDto.toEntry() = Entry(
    timestamp = timestamp.toDateTime(),
    duration = duration.toDuration(),
    finished = finished,
    id = 0
)