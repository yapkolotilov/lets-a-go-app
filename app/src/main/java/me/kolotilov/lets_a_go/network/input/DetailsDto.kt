package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName

data class DetailsDto(
    @SerializedName("user_location")
    val userLocation: CreatePointDto?
)
