package com.fiap.order.driver.web.request

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentHTTPRequest(
    val orderInfo: OrderInfo,
)

data class OrderInfo(
    val number: Long,
    val totalAmount: BigDecimal,
    val lines: List<OrderLine>,
    val orderedAt: LocalDateTime,
    val orderedBy: String,
)

data class OrderLine(
    val name: String,
    val unitPrice: BigDecimal,
    val quantity: Long,
    val unitOfMeasurement: String,
    val totalAmount: BigDecimal,
)
