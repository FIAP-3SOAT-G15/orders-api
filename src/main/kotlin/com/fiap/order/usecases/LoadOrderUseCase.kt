package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import java.util.*

interface LoadOrderUseCase {
    fun getByOrderNumber(orderNumber: Long): Order

    fun findAll(): List<Order>

    fun findByStatus(status: OrderStatus): List<Order>

    fun findByCustomerId(customerId: UUID): List<Order>

    fun findByCustomerIdAndStatus(
        customerId: UUID,
        status: OrderStatus,
    ): List<Order>
}
