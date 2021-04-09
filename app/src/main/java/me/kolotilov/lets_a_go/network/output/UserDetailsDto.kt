package me.kolotilov.lets_a_go.network.output

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Illness
import me.kolotilov.lets_a_go.models.Symptom
import me.kolotilov.lets_a_go.models.UserDetails
import me.kolotilov.lets_a_go.network.input.FilterDto
import me.kolotilov.lets_a_go.network.input.toFilter
import me.kolotilov.lets_a_go.utils.toDateTime
import java.util.*

data class UserDetailsDto(
    @JsonProperty("username")
    val username: String,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("age")
    val age: Int?,
    @JsonProperty("birthDate")
    val birthDate: Date?,
    @JsonProperty("height")
    val height: Int?,
    @JsonProperty("weight")
    val weight: Int?,
    @JsonProperty("illnesses")
    val illnesses: List<String>,
    @JsonProperty("symptoms")
    val symptoms: List<String>,
    @JsonProperty("filter")
    val filter: FilterDto
)

fun UserDetailsDto.toUserDetails() = UserDetails(
    username = username,
    name = name,
    age = age,
    birthDate = birthDate?.toDateTime(),
    height = height,
    weight = weight,
    illnesses = illnesses.map { Illness(it) },
    symptoms = symptoms.map { Symptom(it) },
    filter = filter.toFilter(),
)