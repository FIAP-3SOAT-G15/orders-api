package com.fiap.order.adapter.messaging.sender.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fiap.order.adapter.messaging.sender.PaymentSender
import com.fiap.order.driver.web.request.PaymentRequest
import io.awspring.cloud.sns.core.SnsTemplate
import org.springframework.messaging.support.GenericMessage


class PaymentSenderImpl(private val snsTemplate: SnsTemplate, private val topicName: String, private val mapper: ObjectMapper) : PaymentSender {

    override fun requestPayment(paymentRequest: PaymentRequest) {
        snsTemplate.send(topicName, GenericMessage(mapper.writeValueAsString(paymentRequest)))
    }
}