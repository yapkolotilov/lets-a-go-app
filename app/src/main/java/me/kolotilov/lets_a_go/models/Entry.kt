package me.kolotilov.lets_a_go.models

import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Поход пользователя.
 *
 * @param timestamp Время похода.
 * @param duration Продолжительность похода.
 * @param finished true, если пользователь дошёл до конца маршрута.
 * @param id ID похода.
 */
data class Entry(
    val points: List<Point>,
    val id: Int
) {

    fun finished(route: Route): Boolean {
        return points.last().distance(route.points.last()) < 100
    }

    fun startDate(): DateTime {
        return points.first().timestamp
    }

    fun duration(): Duration {
        return points.duration()
    }

    fun distance(): Double {
        return points.distance()
    }
}