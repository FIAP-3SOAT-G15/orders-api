package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.response.PaymentResponse
import com.fiap.order.usecases.RequestPaymentUseCase
import org.slf4j.LoggerFactory

class PaymentService(
    private val paymentGateway: PaymentGateway,
) : RequestPaymentUseCase
{
    private val log = LoggerFactory.getLogger(javaClass)

    override fun requestPayment(order: Order): PaymentResponse {
        log.info("Requesting payment request for order $order")
        return paymentGateway.requestPayment(order)
    }
}
