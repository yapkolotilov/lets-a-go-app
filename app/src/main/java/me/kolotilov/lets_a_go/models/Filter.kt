package me.kolotilov.lets_a_go.models

import org.joda.time.Duration

/**
 * Фильтр маршрутов.
 *
 * @param maxLength Максимальная длина маршрута (км).
 * @param maxDuration Максимальная продолжительность маршрута.
 * @param typesAllowed Разрешённые типы маршрутов.
 * @param groundsAllowed Разрешённые типы покрытия.
 */
data class Filter(
    val maxLength: Double?,
    val maxDuration: Duration?,
    val typesAllowed: List<Route.Type>?,
    val groundsAllowed: List<Route.Ground>?,
    val id: Int
) {

    companion object {

        /**
         * Возвращает логическое пересечение фильтров.
         */
        fun compose(vararg filters: Filter): Filter {
            return Filter(
                maxLength = filters.mapNotNull { it.maxLength }.minOrNull(),
                maxDuration = filters.mapNotNull { it.maxDuration }.minOrNull(),
                typesAllowed = filters.mapNotNull { it.typesAllowed }.flatten().nullIfEmpty()?.distinct(),
                groundsAllowed = filters.mapNotNull { it.groundsAllowed }.flatten().nullIfEmpty()?.distinct(),
                id = 0
            )
        }

        private fun <T> List<T>.nullIfEmpty(): List<T>? {
            return if (isEmpty()) null else this
        }
    }

    /**
     * Возвращает true, если маршрут удовлетворяет фильтру.
     *
     * @param route Маршрут.
     */
    fun matches(route: Route): Boolean {
        if (maxLength != null && route.distance() > maxLength)
            return false
        if (maxDuration != null && route.duration() > maxDuration)
            return false
        if (typesAllowed != null && !typesAllowed.contains(route.type))
            return false
        if (groundsAllowed != null && !groundsAllowed.contains(route.ground))
            return false
        return true
    }
}