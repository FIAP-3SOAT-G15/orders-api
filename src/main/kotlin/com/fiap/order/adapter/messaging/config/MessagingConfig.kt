package com.fiap.order.adapter.messaging.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fiap.order.adapter.messaging.sender.PaymentSender
import com.fiap.order.adapter.messaging.sender.impl.PaymentSenderImpl
import io.awspring.cloud.sns.core.SnsTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfig {

    @Bean("PaymentSender")
    fun createPaymentSender(snsTemplate: SnsTemplate,
                            @Value("\${sns.topics.request-payment}") topicArn: String,
                            objectMapper: ObjectMapper) : PaymentSender {
        return PaymentSenderImpl(snsTemplate, topicArn, objectMapper)
    }
}