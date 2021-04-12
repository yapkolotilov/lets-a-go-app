package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName

data class CreateEntryPreviewDto(
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("points")
    val points: List<CreatePointDto>
)
