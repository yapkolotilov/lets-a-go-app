package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName("error")
    val error: String
)
