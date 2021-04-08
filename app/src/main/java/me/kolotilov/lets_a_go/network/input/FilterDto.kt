package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.Filter
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.utils.toDate
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class FilterDto(
    @SerializedName("min_length")
    val minLength: Double?,
    @SerializedName("max_length")
    val maxLength: Double?,
    @SerializedName("min_duration")
    val minDuration: Date?,
    @SerializedName("max_duration")
    val maxDuration: Date?,
    @SerializedName("types_allowed")
    val typesAllowed: List<Route.Type>?,
    @SerializedName("grounds_allowed")
    val groundsAllowed: List<Route.Ground>?,
    @SerializedName("enabled")
    val enabled: Boolean
)

fun FilterDto.toFilter() = Filter(
    length = if (minLength != null && maxLength != null) minLength..maxLength else null,
    duration = if (minDuration != null && maxDuration != null) minDuration.toDuration()..maxDuration.toDuration() else null,
    typesAllowed = typesAllowed,
    groundsAllowed = groundsAllowed,
    enabled = enabled,
    id = 0
)

fun Filter.toFilterDto() = FilterDto(
    minLength = length?.start,
    maxLength = length?.endInclusive,
    minDuration = duration?.start?.toDate(),
    maxDuration = duration?.endInclusive?.toDate(),
    typesAllowed = typesAllowed,
    groundsAllowed = groundsAllowed,
    enabled = enabled
)