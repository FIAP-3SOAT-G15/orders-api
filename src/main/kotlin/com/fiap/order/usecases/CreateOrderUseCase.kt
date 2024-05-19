package com.fiap.order.usecases

import com.fiap.order.domain.entities.OrderItem
import com.fiap.order.driver.web.response.PendingOrderResponse
import java.util.*

interface CreateOrderUseCase {
    fun create(customerId: UUID?, items: List<OrderItem>): PendingOrderResponse
}
