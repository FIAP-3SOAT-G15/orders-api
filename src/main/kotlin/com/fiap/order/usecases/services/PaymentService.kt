package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.adapter.gateway.PaymentProviderGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.Payment
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.domain.valueobjects.PaymentStatus
import com.fiap.order.driver.web.request.PaymentRequest
import com.fiap.order.usecases.LoadPaymentUseCase
import com.fiap.order.usecases.ProvidePaymentRequestUseCase
import java.time.LocalDateTime

class PaymentService(
    private val paymentRepository: PaymentGateway,
) :
    LoadPaymentUseCase,
    ProvidePaymentRequestUseCase {
    override fun getByOrderNumber(orderNumber: Long): Payment {
        return paymentRepository.findByOrderNumber(orderNumber)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.PAYMENT_NOT_FOUND,
                message = "Payment not found for order [$orderNumber]",
            )
    }

    override fun providePaymentRequest(order: Order): PaymentRequest {
        return paymentRepository.createFromOrder(order)
    }
}
