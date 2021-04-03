package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.UserDetails
import me.kolotilov.lets_a_go.network.input.FilterDto
import me.kolotilov.lets_a_go.network.input.toFilter
import me.kolotilov.lets_a_go.network.input.toFilterDto

data class UserDetailsDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("age")
    val age: Int? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("weight")
    val weight: Int? = null,
    @SerializedName("illnesses")
    val illnesses: List<String>,
    @SerializedName("symptoms")
    val symptoms: List<String>,
    @SerializedName("filter")
    val filter: FilterDto
)

fun UserDetails.toUserDetailsDto() = UserDetailsDto(
    username = username,
    name = name,
    age = age,
    height = height,
    weight = weight,
    illnesses = illnesses,
    symptoms = symptoms,
    filter = filter.toFilterDto()
)

fun UserDetailsDto.toUserDetails() = UserDetails(
    username = username,
    name = name,
    age = age,
    height = height,
    weight = weight,
    illnesses = illnesses,
    symptoms = symptoms,
    filter = filter.toFilter()
)