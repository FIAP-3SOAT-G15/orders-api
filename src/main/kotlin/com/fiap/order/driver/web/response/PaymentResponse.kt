package com.fiap.order.driver.web.response

import com.fiap.order.domain.entities.Payment
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


fun PaymentResponse.toDomain() = Payment(
    id = id,
    orderNumber = orderNumber,
    externalOrderId = externalOrderId,
    externalOrderGlobalId = externalOrderGlobalId,
    paymentInfo = paymentInfo,
    createdAt = createdAt,
    status = status,
    statusChangedAt = statusChangedAt,
)