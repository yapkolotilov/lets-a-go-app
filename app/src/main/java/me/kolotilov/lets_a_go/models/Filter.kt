package me.kolotilov.lets_a_go.models

import org.joda.time.Duration


/**
 * Фильтр маршрутов.
 *
 * @param length Максимальная длина маршрута (км).
 * @param duration Максимальная продолжительность маршрута.
 * @param typesAllowed Разрешённые типы маршрутов.
 * @param groundsAllowed Разрешённые типы покрытия.
 */
data class Filter(
    val length: ClosedFloatingPointRange<Double>?,
    val duration: ClosedRange<Duration>?,
    val typesAllowed: List<Route.Type>?,
    val groundsAllowed: List<Route.Ground>?,
    val enabled: Boolean,
    val id: Int
)