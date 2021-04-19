package me.kolotilov.lets_a_go.models

/**
 * Ошибка.
 */
data class ServiceException(
    /**
     * Код ошибки.
     */
    val code: ErrorCode,

    /**
     * Статус.
     */
    val status: Int,

    /**
     * Сообщение об ошибке.
     */
    override val message: String
) : Exception()

/**
 * Код ошибки.
 */
enum class ErrorCode {

    /**
     * Неизвестная ошибка.
     */
    OTHER,

    /**
     * Пользователь не существует.
     */
    USER_NOT_EXISTS,

    /**
     * Неправильный email.
     */
    INVALID_USERNAME,

    /**
     * Неправильный пароль.
     */
    INVALID_PASSWORD,

    /**
     * Пользователь уже зарегистрирован.
     */
    USER_ALREADY_EXITS,

    /**
     * Слишком короткий маршрут.
     */
    ENTRY_TOO_SHORT,

    /**
     * Слишком высокая скорость.
     */
    SPEED_TOO_FAST,

    /**
     * Слишком далеко от маршрута.
     */
    TOO_FAR_FROM_ROUTE,

    CONFIRM_EMAIL
}