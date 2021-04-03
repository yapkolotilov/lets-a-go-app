package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Filter
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.utils.toDate
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class FilterDto(
    @SerializedName("max_length")
    val maxLength: Double?,
    @SerializedName("max_duration")
    val maxDuration: Date?,
    @SerializedName("types_allowed")
    val typesAllowed: List<Route.Type>?,
    @SerializedName("grounds_allowed")
    val groundsAllowed: List<Route.Ground>?
)

fun FilterDto.toFilter() = Filter(
    maxLength = maxLength,
    maxDuration = maxDuration?.toDuration(),
    typesAllowed = typesAllowed,
    groundsAllowed = groundsAllowed,
    id = 0
)

fun Filter.toFilterDto() = FilterDto(
    maxLength = maxLength,
    maxDuration = maxDuration?.toDate(),
    typesAllowed = typesAllowed,
    groundsAllowed = groundsAllowed
)