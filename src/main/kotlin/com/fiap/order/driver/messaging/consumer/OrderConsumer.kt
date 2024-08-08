package com.fiap.order.driver.messaging.consumer

import com.fiap.order.domain.valueobjects.PaymentStatus
import com.fiap.order.driver.messaging.event.PaymentEvent
import com.fiap.order.driver.messaging.event.toDomain
import com.fiap.order.usecases.ChangeOrderStatusUseCase
import com.fiap.order.usecases.CreateOrderUseCase
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Component

@Component
class OrderConsumer(
    private val createOrderUseCase: CreateOrderUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase) {

    private val log = LoggerFactory.getLogger(javaClass)

    @SqsListener("\${sqs.queues.request-payment-response}")
    fun onMessage(message: PaymentEvent, @Headers headers: MessageHeaders) {
        log.info("Event from payment response received $message")
        when (message.status) {
            PaymentStatus.PENDING -> createOrderUseCase.acceptPending(message.toDomain())
            PaymentStatus.EXPIRED,
            PaymentStatus.FAILED -> changeOrderStatusUseCase.cancelOrder(message.orderNumber)
            PaymentStatus.CONFIRMED -> changeOrderStatusUseCase.confirmOrder(message.orderNumber)
        }
    }
}