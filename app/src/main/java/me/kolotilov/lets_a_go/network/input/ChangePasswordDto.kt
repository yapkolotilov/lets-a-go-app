package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty


data class ChangePasswordDto(
    @JsonProperty("password")
    val password: String
)
