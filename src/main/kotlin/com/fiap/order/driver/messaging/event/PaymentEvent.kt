package com.fiap.order.driver.messaging.event

import com.fiap.order.domain.entities.Payment
import com.fiap.order.domain.valueobjects.PaymentStatus
import java.time.LocalDateTime

data class PaymentEvent(
    val id: String,
    val orderNumber: Long,
    val externalOrderId: String,
    val externalOrderGlobalId: String?,
    val paymentInfo: String,
    val createdAt: LocalDateTime,
    val status: PaymentStatus,
    val statusChangedAt: LocalDateTime,
)

fun PaymentEvent.toDomain() = Payment(
    id = id,
    orderNumber = orderNumber,
    externalOrderId = externalOrderId,
    externalOrderGlobalId = externalOrderGlobalId,
    paymentInfo = paymentInfo,
    createdAt = createdAt,
    status = status,
    statusChangedAt = statusChangedAt,
)