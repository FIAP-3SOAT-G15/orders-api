package com.fiap.order.driver.database.configuration

import com.fiap.order.OrderApiApp
import com.fiap.order.adapter.gateway.*
import com.fiap.order.adapter.gateway.impl.*
import com.fiap.order.driver.database.persistence.jpa.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [OrderApiApp::class])
class GatewayConfig {

    @Bean("OrderGateway")
    fun createOrderGateway(orderJpaRepository: OrderJpaRepository): OrderGateway {
        return OrderGatewayImpl(orderJpaRepository)
    }

    @Bean("TransactionalGateway")
    fun createTransactionalGateway(): TransactionalGateway {
        return TransactionalGatewayImpl()
    }

    @Bean("ProductGateway")
    fun createProductGateway(productJpaRepository: ProductJpaRepository): ProductGateway {
        return ProductGatewayImpl(productJpaRepository)
    }

    @Bean("CustomerGateway")
    fun createCustomerGateway(customerJpaRepository: CustomerJpaRepository): CustomerGateway {
        return CustomerGatewayImpl(customerJpaRepository)
    }

    @Bean("StockGateway")
    fun createStockGateway(stockJpaRepository: StockJpaRepository): StockGateway {
        return StockGatewayImpl(stockJpaRepository)
    }

    @Bean("PaymentGateway")
    fun createPaymentGateway(paymentJpaRepository: PaymentJpaRepository): PaymentGateway {
        return PaymentGatewayImpl(paymentJpaRepository)
    }
}
