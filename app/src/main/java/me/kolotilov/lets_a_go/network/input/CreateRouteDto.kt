package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Route

data class CreateRouteDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("difficulty")
    val difficulty: Int,
    @SerializedName("type")
    val type: Route.Type?,
    @SerializedName("ground")
    val ground: Route.Ground?,
    @SerializedName("public")
    val isPublic: Boolean,
    @SerializedName("points")
    val points: List<CreatePointDto>
)