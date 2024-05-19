package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order

interface ChangeOrderStatusUseCase {
    fun completeOrder(orderNumber: Long): Order

    fun confirmOrder(orderNumber: Long): Order

    fun startOrderPreparation(orderNumber: Long): Order

    fun finishOrderPreparation(orderNumber: Long): Order

    fun cancelOrder(orderNumber: Long): Order
}
