package com.fiap.order.driver.web.request

class PaymentRequest(
    val externalOrderId: String,
    val externalOrderGlobalId: String?,
    val paymentInfo: String,
)
