package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName


data class LoginDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)
