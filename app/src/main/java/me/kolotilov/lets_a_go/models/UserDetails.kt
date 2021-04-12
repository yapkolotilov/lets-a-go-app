package me.kolotilov.lets_a_go.models

import org.joda.time.DateTime

/**
 * Пользователь.
 *
 * @param username Имя пользователя (e-mail).
 * @param password Пароль.
 * @param confirmationUrl URL подтверждения.
 * @param name ФИО.
 * @param birthDate Возраст.
 * @param height Рост.
 * @param weight Вес.
 * @param illnesses Заболевания.
 * @param symptoms Симптомы.
 * @param filter Фильтр.
 * @param routes Маршруты.
 * @param entries Недавние походы.
 */
data class UserDetails(
    val username: String,
    val name: String?,
    val age: Int?,
    val birthDate: DateTime?,
    val height: Int?,
    val weight: Int?,
    val illnesses: List<Illness>,
    val symptoms: List<Symptom>,
    val filter: Filter,
    val totalDistance: Double,
    val totalKilocaloriesBurnt: Int?,
    val routes: List<RouteItem>,
    val entries: List<RouteEntry>,
)
