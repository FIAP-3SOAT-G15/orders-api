package com.fiap.order.domain.errors

enum class ErrorType {
    CUSTOMER_NOT_FOUND,
    PRODUCT_NOT_FOUND,
    ORDER_NOT_FOUND,

    CUSTOMER_ALREADY_EXISTS,

    EMPTY_ORDER,
    INSUFFICIENT_STOCK,

    INVALID_PRODUCT_CATEGORY,
    INVALID_ORDER_STATUS,
    INVALID_ORDER_STATE_TRANSITION,

    UNEXPECTED_ERROR,
}
