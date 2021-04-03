package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Entry
import me.kolotilov.lets_a_go.utils.toDate
import me.kolotilov.lets_a_go.utils.toDateTime
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class EntryDto(
    @SerializedName("timestamp")
    val timestamp: Date,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("finished")
    val finished: Boolean,
    @SerializedName("id")
    val id: Int
)

fun Entry.toEntryDto() = EntryDto(
    timestamp = timestamp.toDate(),
    duration = duration.toDate(),
    finished = finished,
    id = id
)

fun EntryDto.toEntry() = Entry(
    timestamp = timestamp.toDateTime(),
    duration = duration.toDuration(),
    finished = finished,
    id = id
)