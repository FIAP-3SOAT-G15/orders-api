package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import java.util.*

interface OrderGateway {
    fun findAllActiveOrders(): List<Order>

    fun findByOrderNumber(number: Long): Order?

    fun findByStatus(status: OrderStatus): List<Order>

    fun findByCustomerId(customerId: UUID): List<Order>

    fun findByCustomerIdAndStatus(customerId: UUID, status: OrderStatus): List<Order>

    fun upsert(order: Order): Order

    fun deleteAll()
}
