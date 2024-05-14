package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.PaymentRequest

interface PaymentProviderGateway {
    fun createExternalOrder(order: Order): PaymentRequest
}
