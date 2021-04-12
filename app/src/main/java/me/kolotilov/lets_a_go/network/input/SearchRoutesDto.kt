package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName

data class SearchRoutesDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("filter")
    val filter: FilterDto?,
    @SerializedName("user_location")
    val userLocation: CreatePointDto?
)