package com.fiap.order.driver.database.persistence.provider

import com.fiap.order.adapter.gateway.PaymentProviderGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.PaymentRequest
import java.util.*

class PaymentProviderGatewayMock : PaymentProviderGateway {
    override fun createExternalOrder(order: Order): PaymentRequest {
        return PaymentRequest(
            externalOrderId = UUID.randomUUID().toString(),
            externalOrderGlobalId = null,
            paymentInfo = "mocked"
        )
    }
}
