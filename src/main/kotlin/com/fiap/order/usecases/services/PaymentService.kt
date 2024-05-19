package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.Payment
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.driver.web.request.PaymentRequest
import com.fiap.order.usecases.LoadPaymentUseCase
import com.fiap.order.usecases.ProvidePaymentRequestUseCase
import org.slf4j.LoggerFactory

class PaymentService(
    private val paymentGateway: PaymentGateway,
) :
    LoadPaymentUseCase,
    ProvidePaymentRequestUseCase {
    private val log = LoggerFactory.getLogger(javaClass)
    
    override fun getByOrderNumber(orderNumber: Long): Payment {
        log.info("Requesting payment for order [$orderNumber]")
        return paymentGateway.findByOrderNumber(orderNumber)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.PAYMENT_NOT_FOUND,
                message = "Payment not found for order [$orderNumber]",
            )
    }

    override fun providePaymentRequest(order: Order): PaymentRequest {
        log.info("Requesting payment request for order $order")
        return paymentGateway.createFromOrder(order)
    }
}
