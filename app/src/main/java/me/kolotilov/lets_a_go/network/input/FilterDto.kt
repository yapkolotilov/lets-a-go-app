package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.Filter
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.utils.toDate
import me.kolotilov.lets_a_go.utils.toDuration
import java.util.*

data class FilterDto(
    @JsonProperty("min_length")
    val minLength: Double?,
    @JsonProperty("max_length")
    val maxLength: Double?,
    @JsonProperty("min_duration")
    val minDuration: Date?,
    @JsonProperty("max_duration")
    val maxDuration: Date?,
    @JsonProperty("types_allowed")
    val typesAllowed: List<Route.Type>?,
    @JsonProperty("grounds_allowed")
    val groundsAllowed: List<Route.Ground>?,
    @JsonProperty("enabled")
    val enabled: Boolean
)

fun FilterDto.toFilter() = Filter(
    length = if (minLength != null && maxLength != null) minLength..maxLength else null,
    duration = if (minDuration != null && maxDuration != null) minDuration.toDuration()..maxDuration.toDuration() else null,
    typesAllowed = typesAllowed,
    groundsAllowed = groundsAllowed,
    enabled = enabled,
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