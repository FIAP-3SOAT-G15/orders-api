package com.fiap.order.adapter.controller.configuration

import com.fiap.order.OrderApiApp
import com.fiap.order.adapter.gateway.*
import com.fiap.order.usecases.*
import com.fiap.order.usecases.services.CustomerService
import com.fiap.order.usecases.services.OrderService
import com.fiap.order.usecases.services.ProductService
import com.fiap.order.usecases.services.StockService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import services.PaymentService

@Configuration
@ComponentScan(basePackageClasses = [OrderApiApp::class])
class ServiceConfig {


    @Bean
    fun createProductService(
        productRepository: ProductGateway,
    ): ProductService {
        return ProductService(
            productRepository,
        )
    }

    @Bean
    fun createOrderService(
        orderRepository: OrderGateway,
        loadCustomerUseCase: LoadCustomerUseCase,
        loadProductsUseCase: LoadProductUseCase,
        adjustInventoryUseCase: AdjustStockUseCase,
        providePaymentRequestUseCase: ProvidePaymentRequestUseCase,
        loadPaymentUseCase: LoadPaymentUseCase,
        transactionalRepository: TransactionalGateway,
    ): OrderService {
        return OrderService(
            orderRepository,
            loadCustomerUseCase,
            loadProductsUseCase,
            adjustInventoryUseCase,
            providePaymentRequestUseCase,
            loadPaymentUseCase,
            transactionalRepository,
        )
    }

    @Bean
    fun createStockService(stockRepository: StockGateway): StockService {
        return StockService(stockRepository)
    }

    @Bean
    fun createCustomerService(customerRepository: CustomerGateway): CustomerService {
        return CustomerService(customerRepository)
    }

    @Bean
    fun createPaymentService(
        paymentRepository: PaymentGateway,
        paymentProvider: PaymentProviderGateway,
    ): PaymentService {
        return PaymentService(
            paymentRepository,
            paymentProvider
        )
    }
}
