package nana.study.comments.controllers

import kotlinx.serialization.Contextual
import nana.study.comments.utils.CustomException
import nana.study.comments.utils.toHttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.InstantSource
import java.util.*
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class CustomResponseEntityExceptionHandler(private val timeSource: InstantSource) : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [(CustomException::class)])
    fun handleCustomException(
        ex: CustomException, request: HttpServletRequest
    ): ResponseEntity<Map<String, @Contextual Any>> {
        return ResponseEntity<Map<String, @Contextual Any>>(
            mapOf<String, Any>(
                "timestamp" to Date.from(timeSource.instant()),
                "status" to ex.type.toHttpStatusCode(),
                "error" to ex.message!!,
                "path" to request.requestURI,
                "method" to request.method
            ), ex.type.toHttpStatusCode()
        )
    }
}