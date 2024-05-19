package com.fiap.order.driver.web.response

import com.fiap.order.domain.entities.Order

data class PendingOrderResponse(
    val order: Order,
    val payment: PaymentResponse,
)
