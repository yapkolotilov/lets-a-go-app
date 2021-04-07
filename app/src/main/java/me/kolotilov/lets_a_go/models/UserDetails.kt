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
    val age: Int? = null,
    val birthDate: DateTime? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val illnesses: List<Illness> = emptyList(),
    val symptoms: List<Symptom> = emptyList(),
    val filter: Filter = Filter(null, null, null, null, 0),
    val routes: List<Route> = emptyList(),
    val entries: List<Entry> = emptyList()
)
