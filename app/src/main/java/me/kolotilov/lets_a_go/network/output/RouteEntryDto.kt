package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.RouteEntry
import me.kolotilov.lets_a_go.utils.toDateTime
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class RouteEntryDto(
    @SerializedName("date")
    val date: Date,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("passed")
    val passed: Boolean,
    @SerializedName("route_id")
    val routeId: Int?,
    @SerializedName("id")
    val id: Int
)

fun RouteEntryDto.toRouteEntry() = RouteEntry(
    date = date.toDateTime(),
    duration = duration.toDuration(),
    passed = passed,
    routeId = routeId,
    id = id
)