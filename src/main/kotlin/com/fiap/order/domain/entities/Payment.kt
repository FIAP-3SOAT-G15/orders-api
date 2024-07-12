package com.fiap.order.domain.entities

import com.fiap.order.domain.valueobjects.PaymentStatus
import java.time.LocalDateTime

data class Payment(
    val id: String,
    val orderNumber: Long,
    val externalOrderId: String,
    val externalOrderGlobalId: String?,
    val paymentInfo: String,
    val createdAt: LocalDateTime,
    val status: PaymentStatus,
    val statusChangedAt: LocalDateTime,
)
