package com.fiap.order.adapter.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fiap.order.adapter.messaging.sender.impl.PaymentSenderImpl
import com.fiap.order.createOrder
import com.fiap.order.createPaymentRequest
import com.fiap.order.driver.web.request.PaymentRequest
import io.awspring.cloud.sns.core.SnsTemplate
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.messaging.support.GenericMessage

class PaymentSenderImplTest {

    private val snsTemplate: SnsTemplate = mockk<SnsTemplate>()
    private val topicName: String = "topic"
    private val mapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())

    private val paymentSender = PaymentSenderImpl(
        snsTemplate = snsTemplate,
        topicName = topicName,
        mapper = mapper,
    )

    @Nested
    inner class SendPaymentRequest {

        @Test
        fun `should send payment`() {
            val paymentRequest = createPaymentRequest()

            justRun { snsTemplate.send(eq(topicName), any(GenericMessage::class)) }

            paymentSender.requestPayment(paymentRequest)
        }

    }


}