package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route
import java.util.*

data class RouteItemDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("ground")
    val ground: Route.Ground?,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("duration")
    val duration: Date,
    @SerializedName("distance_to_route")
    val distanceToRoute: Double?,
    @SerializedName("id")
    val id: Int
)