package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.OrderGateway
import com.fiap.order.adapter.gateway.TransactionalGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.OrderItem
import com.fiap.order.domain.entities.OrderLine
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.web.response.PendingOrderResponse
import com.fiap.order.usecases.AdjustStockUseCase
import com.fiap.order.usecases.ChangeOrderStatusUseCase
import com.fiap.order.usecases.CreateOrderUseCase
import com.fiap.order.usecases.LoadCustomerUseCase
import com.fiap.order.usecases.LoadOrderUseCase
import com.fiap.order.usecases.LoadProductUseCase
import com.fiap.order.usecases.RequestPaymentUseCase
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

open class OrderService(
    private val orderGateway: OrderGateway,
    private val getCustomersUseCase: LoadCustomerUseCase,
    private val loadProductUseCase: LoadProductUseCase,
    private val adjustInventoryUseCase: AdjustStockUseCase,
    private val providePaymentRequestUseCase: RequestPaymentUseCase,
    private val transactionalRepository: TransactionalGateway,
) : LoadOrderUseCase,
    CreateOrderUseCase,
    ChangeOrderStatusUseCase
{
    private val log = LoggerFactory.getLogger(javaClass)
    
    override fun getByOrderNumber(orderNumber: Long): Order =
        orderGateway.findByOrderNumber(orderNumber)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.ORDER_NOT_FOUND,
                message = "Order [$orderNumber] not found",
            )

    override fun findAll(): List<Order> =
        orderGateway.findAll()

    override fun findAllActive(): List<Order> =
        orderGateway.findAllActive()

    override fun findByStatus(status: OrderStatus): List<Order> =
        orderGateway.findByStatus(status)

    override fun findByCustomerId(customerId: UUID): List<Order> =
        orderGateway.findByCustomerId(customerId)

    override fun findByStatusAndCustomerId(status: OrderStatus, customerId: UUID): List<Order> =
        orderGateway.findByStatusAndCustomerId(status, customerId)

    override fun create(
        customerId: UUID?,
        items: List<OrderItem>,
    ): PendingOrderResponse {
        return transactionalRepository.transaction {
            if (items.isEmpty()) {
                throw SelfOrderManagementException(errorType = ErrorType.EMPTY_ORDER, message = "Empty order")
            }
            
            val productNumberQuantityMap = items.associate { it.productNumber to it.quantity }

            // Verify existence of all products first
            val products = loadProductUseCase.getByProductNumbers(productNumberQuantityMap.keys.toList())

            // Reserve stock
            adjustInventoryUseCase.decrementStockOfProducts(productNumberQuantityMap)

            val orderLines = productNumberQuantityMap.map { (productNumber, quantity) ->
                val product = products.first { it.number == productNumber }
                OrderLine(
                    number = null,
                    orderNumber = null,
                    productNumber = productNumber,
                    name = product.name,
                    description = product.description,
                    unitPrice = product.price,
                    quantity = quantity,
                    total = BigDecimal.valueOf(quantity).multiply(product.price),
                )
            }

            var order = orderGateway.upsert(
                Order(
                    number = null,
                    orderedAt = LocalDateTime.now(),
                    customer = customerId?.let { getCustomersUseCase.findByCustomerId(customerId) },
                    status = OrderStatus.CREATED,
                    lines = orderLines,
                    total = orderLines.sumOf { it.total },
                )
            )

            order = orderGateway.upsert(
                order.copy(
                    status = OrderStatus.PENDING,
                    lines = order.lines.map { i -> i.copy(orderNumber = order.number) },
                )
            )

            val payment = providePaymentRequestUseCase.requestPayment(order)

            log.info("Created order $order and respective payment $payment")

            PendingOrderResponse(
                order = order,
                payment = payment,
            )
        }
    }

    override fun confirmOrder(orderNumber: Long): Order {
        return transactionalRepository.transaction {
            val order = getByOrderNumber(orderNumber)
            when (order.status) {
                OrderStatus.PENDING -> {
                    log.info("Confirming order $order")
                    orderGateway.upsert(order.copy(status = OrderStatus.CONFIRMED))
                }
                else -> {
                    throw SelfOrderManagementException(
                        errorType = ErrorType.INVALID_ORDER_STATE_TRANSITION,
                        message = "Confirmation is only allowed for orders that are in a pending state",
                    )
                }
            }
        }
    }

    override fun startOrderPreparation(orderNumber: Long): Order {
        return getByOrderNumber(orderNumber)
            .takeIf { it.status == OrderStatus.CONFIRMED }
            ?.let { order ->
                log.info("Starting preparation of order $order")
                orderGateway.upsert(order.copy(status = OrderStatus.PREPARING))
            }
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.INVALID_ORDER_STATE_TRANSITION,
                message = "Preparation of the order cannot begin until it has been confirmed",
            )
    }

    override fun completeOrder(orderNumber: Long): Order {
        return getByOrderNumber(orderNumber)
            .takeIf { it.status == OrderStatus.PREPARING }
            ?.let { order ->
                log.info("Completing order $order")
                orderGateway.upsert(order.copy(status = OrderStatus.COMPLETED))
            }
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.INVALID_ORDER_STATE_TRANSITION,
                message = "Order cannot be completed until it has been prepared",
            )
    }

    override fun finishOrderPreparation(orderNumber: Long): Order {
        return getByOrderNumber(orderNumber)
            .takeIf { it.status == OrderStatus.COMPLETED }
            ?.let { order ->
                log.info("Finishing preparation of order $order")
                orderGateway.upsert(order.copy(status = OrderStatus.DONE))
            }
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.INVALID_ORDER_STATE_TRANSITION,
                message = "Order cannot be finished until it has been completed (delivered)",
            )
    }

    override fun cancelOrder(orderNumber: Long): Order {
        return transactionalRepository.transaction {
            getByOrderNumber(orderNumber)
                .takeIf { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.DONE }
                ?.let { order ->
                    log.info("Cancelling order $order")
                    if (order.status == OrderStatus.CREATED || order.status == OrderStatus.CONFIRMED) {
                        // make reserved stock available again
                        val productNumberQuantityMap = order.lines.associate { it.productNumber to it.quantity }
                        adjustInventoryUseCase.incrementStockOfProducts(productNumberQuantityMap)
                    }
                    orderGateway.upsert(order.copy(status = OrderStatus.CANCELLED))
                }
                ?: throw SelfOrderManagementException(
                    errorType = ErrorType.INVALID_ORDER_STATE_TRANSITION,
                    message = "This order has already been marked as completed",
                )
        }
    }
}
