package com.fiap.order.adapter.controller.configuration

import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(SelfOrderManagementException::class)
    protected fun domainErrorHandler(domainException: SelfOrderManagementException): ResponseEntity<ApiError> {
        val apiErrorResponseEntity: ApiErrorResponseEntity =
            when (domainException.errorType) {
                ErrorType.CUSTOMER_ALREADY_EXISTS,
                ErrorType.INSUFFICIENT_STOCK,
                ->
                    ApiErrorResponseEntity(
                        ApiError(domainException.errorType.name, domainException.message),
                        HttpStatus.UNPROCESSABLE_ENTITY,
                    )

                ErrorType.CUSTOMER_NOT_FOUND,
                ErrorType.PRODUCT_NOT_FOUND,
                ErrorType.ORDER_NOT_FOUND,
                ->
                    ApiErrorResponseEntity(
                        ApiError(domainException.errorType.name, domainException.message),
                        HttpStatus.NOT_FOUND,
                    )

                ErrorType.INVALID_ORDER_STATUS,
                ErrorType.INVALID_ORDER_STATE_TRANSITION,
                ErrorType.INVALID_PRODUCT_CATEGORY,
                ErrorType.EMPTY_ORDER,
                ->
                    ApiErrorResponseEntity(
                        ApiError(domainException.errorType.name, domainException.message),
                        HttpStatus.BAD_REQUEST,
                    )

                else ->
                    ApiErrorResponseEntity(
                        ApiError(ErrorType.UNEXPECTED_ERROR.name, domainException.localizedMessage),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                    )
            }
        return ResponseEntity.status(apiErrorResponseEntity.status).body(apiErrorResponseEntity.body)
    }

    data class ApiError(val error: String, val message: String?)

    data class ApiErrorResponseEntity(val body: ApiError, val status: HttpStatus)
}
