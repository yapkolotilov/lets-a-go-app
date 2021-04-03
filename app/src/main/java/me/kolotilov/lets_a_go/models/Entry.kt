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
        val timestamp: DateTime,
        val duration: Duration,
        val finished: Boolean,
        val id: Int
)