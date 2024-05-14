package com.fiap.order.driver.database.configuration

import com.fiap.order.OrderApiApp
import com.fiap.order.adapter.client.MercadoPagoClient
import com.fiap.order.adapter.gateway.PaymentProviderGateway
import com.fiap.order.driver.database.persistence.provider.MercadoPagoPaymentProvider
import com.fiap.order.driver.database.persistence.provider.PaymentProviderGatewayMock
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [OrderApiApp::class])
class PaymentGatewayConfig {
    @Bean("PaymentProvider")
    @ConditionalOnProperty("payment-provider.mock", havingValue = "false")
    fun createPaymentProvider(
        mercadoPagoClient: MercadoPagoClient,
        @Value("\${mercadopago.integration.webhookBaseUrl}") webhookBaseUrl: String,
    ): PaymentProviderGateway {
        return MercadoPagoPaymentProvider(
            mercadoPagoClient,
            webhookBaseUrl,
        )
    }

    @Bean("PaymentProvider")
    @ConditionalOnProperty("payment-provider.mock", havingValue = "true")
    fun createPaymentProviderMock(): PaymentProviderGateway {
        return PaymentProviderGatewayMock()
    }
}
