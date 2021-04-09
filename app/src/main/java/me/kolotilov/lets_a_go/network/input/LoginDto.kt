package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty


data class LoginDto(
    @JsonProperty("username")
    val username: String,
    @JsonProperty("password")
    val password: String
)
