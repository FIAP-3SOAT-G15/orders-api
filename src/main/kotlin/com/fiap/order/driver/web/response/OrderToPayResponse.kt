package com.fiap.order.driver.web.response

import com.fiap.order.domain.entities.Order

data class OrderToPayResponse(
    val order: Order,
    val paymentInfo: String,
)
