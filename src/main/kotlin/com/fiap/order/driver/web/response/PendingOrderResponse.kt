package com.fiap.order.driver.web.response

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.Payment

data class PendingOrderResponse(
    val order: Order,
    val payment: Payment?,
)
