package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order

interface ConfirmOrderUseCase {
    fun confirmOrder(orderNumber: Long): Order
}
