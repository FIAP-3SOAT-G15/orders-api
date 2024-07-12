package com.fiap.order.adapter.messaging.sender

import com.fiap.order.driver.web.request.PaymentRequest

interface PaymentSender {
    fun requestPayment(paymentRequest:PaymentRequest)
}