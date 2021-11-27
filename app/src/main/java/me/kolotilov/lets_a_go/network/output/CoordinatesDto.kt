package me.kolotilov.lets_a_go.network.output

import com.google.gson.annotations.SerializedName

class CoordinatesDto(
    @SerializedName("topLeft")
    val topLeft: CoordinateDto,
    @SerializedName("bottomRight")
    val bottomRight: CoordinateDto
)