package com.fiap.order.adapter.controller

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.web.OrdersAPI
import com.fiap.order.driver.web.request.OrderRequest
import com.fiap.order.driver.web.response.PendingOrderResponse
import com.fiap.order.usecases.ChangeOrderStatusUseCase
import com.fiap.order.usecases.CreateOrderUseCase
import com.fiap.order.usecases.LoadOrderUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.ClaimAccessor
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class OrderController(
    private val loadOrderUseCase: LoadOrderUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
) : OrdersAPI
{
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getByOrderNumber(orderNumber: Long): ResponseEntity<Order> =
        ResponseEntity.ok(loadOrderUseCase.getByOrderNumber(orderNumber))

    override fun findAllActive(): ResponseEntity<List<Order>> =
        ResponseEntity.ok(loadOrderUseCase.findAllActive())

    override fun findAll(): ResponseEntity<List<Order>> =
        ResponseEntity.ok(loadOrderUseCase.findAll())

    override fun findByStatus(status: String): ResponseEntity<List<Order>> =
        ResponseEntity.ok(loadOrderUseCase.findByStatus(OrderStatus.fromString(status)))

    override fun findByStatusAndCustomerId(status: String, customerId: String, ): ResponseEntity<List<Order>> {
        val orderStatus = OrderStatus.fromString(status)
        customerId
            .runCatching { UUID.fromString(this) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .let { loadOrderUseCase.findByStatusAndCustomerId(orderStatus, it) }
            .run { return ResponseEntity.ok(this) }
    }

    override fun findByCustomerId(customerId: String): ResponseEntity<List<Order>> =
        customerId
            .runCatching { UUID.fromString(this) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .let { loadOrderUseCase.findByCustomerId(it) }
            .run { return ResponseEntity.ok(this) }

    override fun create(orderRequest: OrderRequest): ResponseEntity<PendingOrderResponse> =
        ResponseEntity.ok(
            createOrderUseCase.create(
                customerId = getAuthenticatedCustomerId(),
                items = orderRequest.toOrderItemsDomain()
            )
        )

    override fun start(orderNumber: Long): ResponseEntity<Order> =
        ResponseEntity.ok(changeOrderStatusUseCase.startOrderPreparation(orderNumber))

    override fun finish(orderNumber: Long): ResponseEntity<Order> =
        ResponseEntity.ok(changeOrderStatusUseCase.finishOrderPreparation(orderNumber))

    override fun complete(orderNumber: Long): ResponseEntity<Order> =
        ResponseEntity.ok(changeOrderStatusUseCase.completeOrder(orderNumber))

    override fun cancel(orderNumber: Long): ResponseEntity<Order> =
        ResponseEntity.ok(changeOrderStatusUseCase.cancelOrder(orderNumber))

    override fun confirm(orderNumber: Long): ResponseEntity<Order> =
        ResponseEntity.ok(changeOrderStatusUseCase.confirmOrder(orderNumber))
    
    private fun getAuthenticatedCustomerId(): UUID? =
        runCatching {  
            UUID.fromString(
                (SecurityContextHolder.getContext().authentication.credentials as ClaimAccessor)
                    .getClaim("custom:CUSTOMER_ID")
            )
        }.getOrElse {
            log.error(it.message, it.cause)
            null
        }
}
