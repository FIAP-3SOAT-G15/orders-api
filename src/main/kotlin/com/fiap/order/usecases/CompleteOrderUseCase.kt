package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order

interface CompleteOrderUseCase {
    fun completeOrder(orderNumber: Long): Order
}
