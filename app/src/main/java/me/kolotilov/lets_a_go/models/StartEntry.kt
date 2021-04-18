package me.kolotilov.lets_a_go.models

data class StartEntry(
    val id: Int,
    val name: String?,
    val points: List<Point>,
)