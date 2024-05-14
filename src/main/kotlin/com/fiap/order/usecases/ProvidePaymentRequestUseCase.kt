package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.PaymentRequest

interface ProvidePaymentRequestUseCase {
    fun providePaymentRequest(order: Order): PaymentRequest
}
