package me.kolotilov.lets_a_go.models

/**
 * Ошибка.
 */
class ServiceException(
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
    ROUTE_TOO_SHORT,
}