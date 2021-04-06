package me.kolotilov.lets_a_go.network.input

import com.google.gson.annotations.SerializedName
import me.kolotilov.lets_a_go.models.ErrorCode
import me.kolotilov.lets_a_go.models.ServiceException

/**
 * Ошибка
 */
data class ErrorDto(
    //@ApiModelProperty("Код ошибки.")
    @SerializedName("code")
    val code: ErrorCode,
    //@ApiModelProperty("Статус ошибки.")
    @SerializedName("status")
    val status: Int,
    //@ApiModelProperty("Сообщение об ощибке.")
    @SerializedName("message")
    val message: String,
    //@ApiModelProperty("Трассировка стека.")
    @SerializedName("stackTrace")
    val stackTrace: String
)

fun ErrorDto.toServiceException(): ServiceException {
    return ServiceException(
        code = code,
        status = status,
        message = message
    )
}