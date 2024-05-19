package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.PaymentsApiClient
import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.OrderInfo
import com.fiap.order.driver.web.request.OrderLine
import com.fiap.order.driver.web.request.PaymentHTTPRequest
import com.fiap.order.driver.web.response.PaymentResponse

class PaymentGatewayImpl(
    private val paymentsApiClient: PaymentsApiClient
) : PaymentGateway {

    override fun findByPaymentId(paymentId: String): PaymentResponse {
        return paymentsApiClient.getByPaymentId(paymentId)
    }

    override fun requestPayment(order: Order): PaymentResponse {
        val paymentHTTPRequest = PaymentHTTPRequest(
            orderInfo = OrderInfo(
                number = order.number!!,
                orderedAt = order.orderedAt,
                orderedBy = order.customer?.name ?: ANONYMOUS,
                totalAmount = order.total,
                lines = order.items.map { product ->
                    // TODO: each product as order line for now, fix later
                    OrderLine(
                        name = product.name,
                        quantity = 1,
                        unitPrice = product.price,
                        unitOfMeasurement = "unit",
                        totalAmount = product.price
                    )
                }
            )
        )
        
        return paymentsApiClient.create(paymentHTTPRequest)
    }
    
    companion object {
        const val ANONYMOUS = "Anonymous"
    }
}
