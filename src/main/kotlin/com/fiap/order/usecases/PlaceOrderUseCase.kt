package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.OrderItem
import java.util.*

interface PlaceOrderUseCase {
    fun create(
        customerId: UUID?,
        items: List<OrderItem>,
    ): Order
}
