package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Illness
import me.kolotilov.lets_a_go.models.Symptom
import me.kolotilov.lets_a_go.models.UserDetails
import me.kolotilov.lets_a_go.network.input.FilterDto
import me.kolotilov.lets_a_go.network.input.toFilter
import me.kolotilov.lets_a_go.utils.toDateTime
import java.util.*

data class UserDetailsDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("birthDate")
    val birthDate: Date?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("weight")
    val weight: Int?,
    @SerializedName("illnesses")
    val illnesses: List<String>,
    @SerializedName("symptoms")
    val symptoms: List<String>,
    @SerializedName("filter")
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
    filter = filter.toFilter()
)