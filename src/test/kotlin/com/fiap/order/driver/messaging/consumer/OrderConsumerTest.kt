package com.fiap.order.driver.messaging.consumer

import com.fiap.order.createOrder
import com.fiap.order.createPaymentEvent
import com.fiap.order.domain.entities.Payment
import com.fiap.order.domain.valueobjects.PaymentStatus
import com.fiap.order.driver.messaging.event.toDomain
import com.fiap.order.driver.web.response.PendingOrderResponse
import com.fiap.order.usecases.ChangeOrderStatusUseCase
import com.fiap.order.usecases.CreateOrderUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.messaging.MessageHeaders

class OrderConsumerTest {

    private val createOrderUseCase = mockk<CreateOrderUseCase>()
    private val changeOrderStatusUseCase = mockk<ChangeOrderStatusUseCase>()

    private val consumer = OrderConsumer(
        createOrderUseCase, changeOrderStatusUseCase
    )

    @Nested
    inner class ConsumerPaymentResponse {


        @Test
        fun `should consumes payment pending`() {
            val paymentEvent = createPaymentEvent(status = PaymentStatus.PENDING)

            every { createOrderUseCase.acceptPending(any(Payment::class)) } returns PendingOrderResponse(
                createOrder(), paymentEvent.toDomain()
            )

            consumer.onMessage(paymentEvent, MessageHeaders(emptyMap()))

            verify { createOrderUseCase.acceptPending(paymentEvent.toDomain()) }
        }

        @Test
        fun `should consumes payment confirmed`() {
            val paymentEvent = createPaymentEvent(status = PaymentStatus.CONFIRMED)

            every { changeOrderStatusUseCase.confirmOrder(any()) } returns createOrder()

            consumer.onMessage(paymentEvent, MessageHeaders(emptyMap()))

            verify { changeOrderStatusUseCase.confirmOrder(paymentEvent.orderNumber) }

        }

        @Test
        fun `should consumes payment failed`() {
            val paymentEvent = createPaymentEvent(status = PaymentStatus.FAILED)

            every { changeOrderStatusUseCase.cancelOrder(any()) } returns createOrder()

            consumer.onMessage(paymentEvent, MessageHeaders(emptyMap()))

            verify { changeOrderStatusUseCase.cancelOrder(paymentEvent.orderNumber) }
        }

        @Test
        fun `should consumes payment expired`() {
            val paymentEvent = createPaymentEvent(status = PaymentStatus.EXPIRED)

            every { changeOrderStatusUseCase.cancelOrder(any()) } returns createOrder()

            consumer.onMessage(paymentEvent, MessageHeaders(emptyMap()))

            verify { changeOrderStatusUseCase.cancelOrder(paymentEvent.orderNumber) }
        }

    }

}