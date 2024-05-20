package com.fiap.order.driver.web.request

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentRequest(
    val orderInfo: PaymentOrderInfo,
)

data class PaymentOrderInfo(
    val number: Long,
    val total: BigDecimal,
    val lines: List<PaymentOrderInfoLine>,
    val orderedAt: LocalDateTime,
    val orderedBy: String,
)

data class PaymentOrderInfoLine(
    val name: String,
    val unitPrice: BigDecimal,
    val quantity: Long,
    val total: BigDecimal,
)
