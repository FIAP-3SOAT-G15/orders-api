package com.fiap.order.domain.entities

import com.fiap.order.domain.valueobjects.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class Order(
    val number: Long? = null,
    val orderedAt: LocalDateTime = LocalDateTime.now(),
    val customer: Customer? = null,
    val status: OrderStatus = OrderStatus.CREATED,
    val lines: List<OrderLine>,
    val total: BigDecimal,
)
