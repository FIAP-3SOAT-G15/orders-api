package com.fiap.order.adapter.controller

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.web.OrdersAPI
import com.fiap.order.driver.web.request.OrderRequest
import com.fiap.order.driver.web.response.PendingOrderResponse
import com.fiap.order.usecases.ChangeOrderStatusUseCase
import com.fiap.order.usecases.LoadOrderUseCase
import com.fiap.order.usecases.CreateOrderUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class OrderController(
    private val loadOrdersUseCase: LoadOrderUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
) : OrdersAPI
{
    private val log = LoggerFactory.getLogger(javaClass)

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

    override fun create(orderRequest: OrderRequest): ResponseEntity<PendingOrderResponse> {
        var customerId: UUID? = null
        try {
            customerId = UUID.fromString(
                (SecurityContextHolder.getContext().authentication.credentials as ClaimAccessor)
                    .getClaim("custom:CUSTOMER_ID")
            )
        } catch (ex: Exception) {
            log.error(ex.message, ex.cause)
        }

        val orderToPayResponse = createOrderUseCase.create(customerId, orderRequest.toOrderItemsDomain())

        return ResponseEntity.ok(orderToPayResponse)
    }

    override fun start(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(changeOrderStatusUseCase.startOrderPreparation(orderNumber))
    }

    override fun finish(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(changeOrderStatusUseCase.finishOrderPreparation(orderNumber))
    }

    override fun complete(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(changeOrderStatusUseCase.completeOrder(orderNumber))
    }

    override fun cancel(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(changeOrderStatusUseCase.cancelOrder(orderNumber))
    }

    override fun confirm(orderNumber: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(changeOrderStatusUseCase.confirmOrder(orderNumber))
    }
}
