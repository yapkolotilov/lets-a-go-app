package me.kolotilov.lets_a_go.network.output

import com.fasterxml.jackson.annotation.JsonProperty


data class TokenDto(
    @JsonProperty("token")
    val token: String
)
