package com.fiap.order.driver.database.persistence.provider

import com.fiap.order.adapter.client.MercadoPagoClient
import com.fiap.order.adapter.client.MercadoPagoQRCodeOrderRequest
import com.fiap.order.adapter.client.MercadoPagoQRCodeOrderRequestItem
import com.fiap.order.adapter.gateway.PaymentProviderGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.PaymentRequest

class MercadoPagoPaymentProvider(
    private val mercadoPagoClient: MercadoPagoClient,
    private val webhookBaseUrl: String,
) : PaymentProviderGateway {

    override fun createExternalOrder(order: Order): PaymentRequest {
        // source_news=ipn indicates application will receive only Instant Payment Notifications (IPNs), not webhooks
        val notificationUrl = "${webhookBaseUrl}/payments/notifications/${order.number}?source_news=ipn"

        val response =
            mercadoPagoClient.submitMerchantOrder(
                MercadoPagoQRCodeOrderRequest(
                    title = "Order ${order.number}",
                    description = "Ordered at ${order.date} by ${order.customer?.name ?: order.customer?.document ?: "anonymous"}",
                    externalReference = order.number.toString(),
                    notificationUrl = notificationUrl,
                    totalAmount = order.total,
                    items =
                    order.items.map { product ->
                        MercadoPagoQRCodeOrderRequestItem(
                            title = product.name,
                            unitPrice = product.price,
                            quantity = 1, // TODO: fix to use order lines with persisted quantities per product
                            unitMeasure = MercadoPagoMeasureUnit.UNIT.measureUnit,
                            totalAmount = product.price,
                        )
                    },
                ),
            )

        return PaymentRequest(
            externalOrderId = response.inStoreOrderId,
            externalOrderGlobalId = null,
            paymentInfo = response.qrData,
        )
    }

    enum class MercadoPagoMeasureUnit(val measureUnit: String) {
        UNIT("unit"),
    }
}
