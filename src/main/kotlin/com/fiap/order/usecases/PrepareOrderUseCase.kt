package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order

interface PrepareOrderUseCase {
    fun startOrderPreparation(orderNumber: Long): Order

    fun finishOrderPreparation(orderNumber: Long): Order
}
