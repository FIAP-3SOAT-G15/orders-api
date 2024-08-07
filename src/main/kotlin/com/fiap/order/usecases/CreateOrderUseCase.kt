package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.OrderItem
import com.fiap.order.domain.entities.Payment
import com.fiap.order.domain.valueobjects.PaymentStatus
import com.fiap.order.driver.messaging.event.PaymentEvent
import com.fiap.order.driver.web.response.PendingOrderResponse
import java.util.*

interface CreateOrderUseCase {
    fun requestCreate(customerId: UUID?, items: List<OrderItem>): PendingOrderResponse
    fun acceptPending(payment: Payment) : PendingOrderResponse
}
