package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order

interface CancelOrderStatusUseCase {
    fun cancelOrder(orderNumber: Long): Order
}
