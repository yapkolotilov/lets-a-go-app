package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import java.util.*

data class EditDetailsDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("birthDate")
    val birthDate: Date?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("weight")
    val weight: Int?,
    @SerializedName("illnesses")
    val illnesses: List<String>?,
    @SerializedName("symptoms")
    val symptoms: List<String>?,
    @SerializedName("filter")
    val filter: FilterDto?,
    @SerializedName("update_filter")
    val updateFilter: Boolean
)