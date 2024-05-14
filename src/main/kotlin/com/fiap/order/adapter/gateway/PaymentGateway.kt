package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Payment


interface PaymentGateway {
    fun findByOrderNumber(orderNumber: Long): Payment?

    fun create(payment: Payment): Payment
}
