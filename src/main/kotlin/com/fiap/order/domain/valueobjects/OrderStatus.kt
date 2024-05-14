package com.fiap.order.domain.valueobjects

import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException

enum class OrderStatus {
    CREATED,
    PENDING,
    CONFIRMED,
    PREPARING,
    COMPLETED,
    DONE,
    CANCELLED,
    ;

    companion object {
        fun fromString(status: String): OrderStatus {
            return values().firstOrNull { it.name.equals(status.trim(), ignoreCase = true) }
                ?: throw SelfOrderManagementException(
                    errorType = ErrorType.INVALID_ORDER_STATUS,
                    message = "Status $status is not valid",
                )
        }
    }
}
