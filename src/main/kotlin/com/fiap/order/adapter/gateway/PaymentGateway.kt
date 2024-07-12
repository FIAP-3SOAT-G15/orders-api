package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.response.PaymentResponse

interface PaymentGateway {
    fun findByPaymentId(paymentId: String): PaymentResponse?

    fun notifyRequestPayment(order: Order)
}
