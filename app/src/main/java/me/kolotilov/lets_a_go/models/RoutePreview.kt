package me.kolotilov.lets_a_go.models

import org.joda.time.Duration

/**
 * Превью маршрута.
 *
 * @param distance Продолжительность маршрута.
 * @param duration Продолжительность.
 * @param kiloCaloriesBurnt Килокалорий сожжено.
 * @param altitudeDelta Перепад высот.
 * @param type Тип маршрута.
 * @param speed Скорость (км/ч).
 * @param difficulty Сложность.
 */
data class RoutePreview(
    val distance: Double,
    val duration: Duration,
    val speed: Double,
    val kiloCaloriesBurnt: Int?,
    val altitudeDelta: Double,
    val type: Route.Type,
    val difficulty: Int
)
