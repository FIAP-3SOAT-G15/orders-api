package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.Payment
import com.fiap.order.driver.web.request.PaymentRequest


interface PaymentGateway {
    fun findByOrderNumber(orderNumber: Long): Payment?

    fun create(payment: Payment): Payment

    fun createFromOrder(order: Order): PaymentRequest
}
