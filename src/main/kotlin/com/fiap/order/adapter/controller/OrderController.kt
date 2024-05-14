package com.fiap.order.adapter.controller

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.web.OrdersAPI
import com.fiap.order.driver.web.request.OrderRequest
import com.fiap.order.driver.web.response.OrderToPayResponse
import com.fiap.order.usecases.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class OrderController(
    private val loadOrdersUseCase: LoadOrderUseCase,
    private val createOrderUseCase: PlaceOrderUseCase,
    private val loadPaymentUseCase: LoadPaymentUseCase,
    private val prepareOrderUseCase: PrepareOrderUseCase,
    private val completeOrderUseCase: CompleteOrderUseCase,
    private val cancelOrderStatusUseCase: CancelOrderStatusUseCase,
) : OrdersAPI {
    override fun getByOrderNumber(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(loadOrdersUseCase.getByOrderNumber(orderNumber))
    }

    override fun findAll(): ResponseEntity<List<Order>> {
        return ResponseEntity.ok(loadOrdersUseCase.findAll())
    }

    override fun getByStatus(status: String): ResponseEntity<List<Order>> {
        return ResponseEntity.ok(loadOrdersUseCase.findByStatus(OrderStatus.fromString(status)))
    }

    override fun getByStatusAndCustomerId(
        status: String,
        customerId: String,
    ): ResponseEntity<List<Order>> {
        val orderStatus = OrderStatus.fromString(status)
        customerId
            .runCatching { UUID.fromString(this) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .let { loadOrdersUseCase.findByCustomerIdAndStatus(it, orderStatus) }
            .run { return ResponseEntity.ok(this) }
    }

    override fun getByCustomerId(customerId: String): ResponseEntity<List<Order>> {
        customerId
            .runCatching { UUID.fromString(this) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .let { loadOrdersUseCase.findByCustomerId(it) }
            .run { return ResponseEntity.ok(this) }
    }

    override fun create(orderRequest: OrderRequest): ResponseEntity<OrderToPayResponse> {
        var customerId: UUID? = null
        try {
            customerId = UUID.fromString(
                (SecurityContextHolder.getContext().authentication.credentials as ClaimAccessor).getClaim<String>("custom:CUSTOMER_ID")
            )
        } catch (_: Exception) {
        }

        val order =
            createOrderUseCase.create(
                customerId,
                orderRequest.toOrderItemDomain(),
            )
        val payment = loadPaymentUseCase.getByOrderNumber(order.number!!)

        return ResponseEntity.ok(
            OrderToPayResponse(
                order = order,
                paymentInfo = payment.paymentInfo,
            ),
        )
    }

    override fun start(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(prepareOrderUseCase.startOrderPreparation(orderNumber))
    }

    override fun finish(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(prepareOrderUseCase.finishOrderPreparation(orderNumber))
    }

    override fun complete(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(completeOrderUseCase.completeOrder(orderNumber))
    }

    override fun cancel(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(cancelOrderStatusUseCase.cancelOrder(orderNumber))
    }
}
