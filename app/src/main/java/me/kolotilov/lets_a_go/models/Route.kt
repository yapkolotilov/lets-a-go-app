package me.kolotilov.lets_a_go.models

import org.joda.time.Duration

/**
 * Маршрут.
 *
 * @param name Название.
 * @param difficulty Сложность от 1 до 5.
 * @param type Тип маршрута.
 * @param ground Тип покрытия.
 * @param points Точки маршрута.
 * @param entries Походы пользователей.
 * @param id ID маршрута.
 */
data class Route(
    val name: String?,
    val difficulty: Int?,
    val type: Type?,
    val ground: Ground?,
    val points: List<Point>,
    val entries: List<Entry>,
    val id: Int,
) {

    /**
     * Тип маршрута.
     */
    enum class Type {
        /**
         * Ходьба.
         */
        WALKING,

        /**
         * Бег.
         */
        RUNNING,

        /**
         * Велосипед.
         */
        CYCLING
    }

    /**
     * Тип покрытия.
     */
    enum class Ground {
        /**
         * Асфальт.
         */
        ASPHALT,

        /**
         * Пересечённая местность.
         */
        TRACK
    }

    /**
     * Длина маршрута.
     */
    fun distance(): Double = points.distance()

    /**
     * Продолжительность маршрута.
     */
    fun duration(): Duration = points.duration()
}

