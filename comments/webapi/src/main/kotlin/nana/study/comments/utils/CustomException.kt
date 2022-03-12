package nana.study.comments.utils

import nana.study.comments.utils.CustomExceptionType.ALREADY_EXIST
import nana.study.comments.utils.CustomExceptionType.NOT_EXIST
import org.springframework.http.HttpStatus


enum class CustomExceptionType {
    ALREADY_EXIST,
    NOT_EXIST
}

fun CustomExceptionType.toHttpStatusCode(): HttpStatus =
    when (this) {
        ALREADY_EXIST -> HttpStatus.CONFLICT
        NOT_EXIST -> HttpStatus.BAD_REQUEST
    }

class CustomException(val type: CustomExceptionType, message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    constructor(type: CustomExceptionType, cause: Throwable) : this(type, null, cause)
}