package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName

data class CoordinateDto(
    @SerializedName("latitude")
    val latitude: Long,
    @SerializedName("longitude")
    val longitude: Long
)