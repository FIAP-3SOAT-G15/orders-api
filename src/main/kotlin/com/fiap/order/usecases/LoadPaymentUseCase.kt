package com.fiap.order.usecases

import com.fiap.order.domain.entities.Payment

interface LoadPaymentUseCase {
    fun getByOrderNumber(orderNumber: Long): Payment
}
