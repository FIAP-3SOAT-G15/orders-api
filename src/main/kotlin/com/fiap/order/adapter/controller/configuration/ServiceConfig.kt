package com.fiap.order.adapter.controller.configuration

import com.fiap.order.OrderApiApp
import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.adapter.gateway.OrderGateway
import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.adapter.gateway.TransactionalGateway
import com.fiap.order.usecases.AdjustStockUseCase
import com.fiap.order.usecases.LoadCustomerUseCase
import com.fiap.order.usecases.LoadProductUseCase
import com.fiap.order.usecases.RequestPaymentUseCase
import com.fiap.order.usecases.services.CustomerService
import com.fiap.order.usecases.services.OrderService
import com.fiap.order.usecases.services.PaymentService
import com.fiap.order.usecases.services.ProductService
import com.fiap.order.usecases.services.StockService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [OrderApiApp::class])
class ServiceConfig {

    @Bean
    fun createProductService(productRepository: ProductGateway): ProductService {
        return ProductService(productRepository)
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
    fun createPaymentService(paymentRepository: PaymentGateway): PaymentService {
        return PaymentService(paymentRepository)
    }

    @Bean
    fun createOrderService(
        orderRepository: OrderGateway,
        loadCustomerUseCase: LoadCustomerUseCase,
        loadProductsUseCase: LoadProductUseCase,
        adjustInventoryUseCase: AdjustStockUseCase,
        providePaymentRequestUseCase: RequestPaymentUseCase,
        transactionalRepository: TransactionalGateway,
    ): OrderService {
        return OrderService(
            orderRepository,
            loadCustomerUseCase,
            loadProductsUseCase,
            adjustInventoryUseCase,
            providePaymentRequestUseCase,
            transactionalRepository,
        )
    }
}
