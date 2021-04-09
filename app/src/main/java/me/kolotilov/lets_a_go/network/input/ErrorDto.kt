package me.kolotilov.lets_a_go.network.input

import com.fasterxml.jackson.annotation.JsonProperty
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.models.ServiceException

/**
 * Ошибка
 */
data class ErrorDto(
    //@ApiModelProperty("Код ошибки.")
    @JsonProperty("code")
    val code: ErrorCode,
    //@ApiModelProperty("Статус ошибки.")
    @JsonProperty("status")
    val status: Int,
    //@ApiModelProperty("Сообщение об ощибке.")
    @JsonProperty("message")
    val message: String,
    //@ApiModelProperty("Трассировка стека.")
    @JsonProperty("stackTrace")
    val stackTrace: String
)

fun ErrorDto.toServiceException(): ServiceException {
    return ServiceException(
        code = code,
        status = status,
        message = message
    )
}