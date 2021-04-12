package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName


data class TokenDto(
    @SerializedName("token")
    val token: String
)
