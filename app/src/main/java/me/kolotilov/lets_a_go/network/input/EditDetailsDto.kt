package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class EditDetailsDto(
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("birthDate")
    val birthDate: Date?,
    @JsonProperty("height")
    val height: Int?,
    @JsonProperty("weight")
    val weight: Int?,
    @JsonProperty("illnesses")
    val illnesses: List<String>?,
    @JsonProperty("symptoms")
    val symptoms: List<String>?,
    @JsonProperty("filter")
    val filter: FilterDto?,
    @JsonProperty("update_filter")
    val updateFilter: Boolean
)