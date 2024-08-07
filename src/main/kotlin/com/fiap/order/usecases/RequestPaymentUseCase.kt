package com.fiap.order.usecases

import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.response.PaymentResponse

interface RequestPaymentUseCase {
    fun requestPayment(order: Order)
}
