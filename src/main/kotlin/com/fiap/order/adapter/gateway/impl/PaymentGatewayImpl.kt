package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.PaymentsApiClient
import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.adapter.messaging.sender.PaymentSender
import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.PaymentOrderInfo
import com.fiap.order.driver.web.request.PaymentOrderInfoLine
import com.fiap.order.driver.web.request.PaymentRequest
import com.fiap.order.driver.web.response.PaymentResponse

class PaymentGatewayImpl(
    private val paymentsApiClient: PaymentsApiClient,
    private val paymentSender: PaymentSender
) : PaymentGateway {

    override fun findByPaymentId(paymentId: String): PaymentResponse {
        return paymentsApiClient.getByPaymentId(paymentId)
    }

    override fun requestPayment(order: Order): PaymentResponse {
        val paymentHTTPRequest = PaymentRequest(
            orderInfo = PaymentOrderInfo(
                number = order.number!!,
                orderedAt = order.orderedAt,
                orderedBy = order.customer?.name ?: ANONYMOUS,
                total = order.total,
                lines = order.lines.map { orderLine ->
                    PaymentOrderInfoLine(
                        name = orderLine.name,
                        quantity = orderLine.quantity,
                        unitPrice = orderLine.unitPrice,
                        total = orderLine.total
                    )
                }
            )
        )

        return paymentsApiClient.create(paymentHTTPRequest)
    }

    override fun notifyRequestPayment(order: Order) {
        paymentSender.requestPayment(
            paymentRequest = PaymentRequest(
                orderInfo = PaymentOrderInfo(
                    number = order.number!!,
                    orderedAt = order.orderedAt,
                    orderedBy = order.customer?.name ?: ANONYMOUS,
                    total = order.total,
                    lines = order.lines.map { orderLine ->
                        PaymentOrderInfoLine(
                            name = orderLine.name,
                            quantity = orderLine.quantity,
                            unitPrice = orderLine.unitPrice,
                            total = orderLine.total
                        )
                    }
                )
            )
        )
    }

    companion object {
        const val ANONYMOUS = "Anonymous"
    }
}
