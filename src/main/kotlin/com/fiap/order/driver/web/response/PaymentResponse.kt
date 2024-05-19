package com.fiap.order.driver.web.response

import com.fiap.order.domain.valueobjects.PaymentStatus
import java.time.LocalDateTime

data class PaymentResponse(
    val id: String,
    val orderNumber: Long,
    val externalOrderId: String,
    val externalOrderGlobalId: String?,
    val paymentInfo: String,
    val createdAt: LocalDateTime,
    val status: PaymentStatus,
    val statusChangedAt: LocalDateTime,
)
