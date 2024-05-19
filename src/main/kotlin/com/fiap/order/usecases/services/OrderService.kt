package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.OrderGateway
import com.fiap.order.adapter.gateway.TransactionalGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.OrderItem
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
import java.time.LocalDateTime
import java.util.*

open class OrderService(
    private val orderRepository: OrderGateway,
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
    
    override fun getByOrderNumber(orderNumber: Long): Order {
        return orderRepository.findByOrderNumber(orderNumber)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.ORDER_NOT_FOUND,
                message = "Order [$orderNumber] not found",
            )
    }

    override fun findAll(): List<Order> {
        return orderRepository.findAllActiveOrders()
    }

    override fun findByStatus(status: OrderStatus): List<Order> {
        return orderRepository.findByStatus(status)
    }

    override fun findByCustomerId(customerId: UUID): List<Order> {
        return orderRepository.findByCustomerId(customerId)
    }

    override fun findByCustomerIdAndStatus(customerId: UUID, status: OrderStatus): List<Order> {
        return orderRepository.findByCustomerIdAndStatus(customerId, status)
    }

    override fun create(
        customerId: UUID?,
        items: List<OrderItem>,
    ): PendingOrderResponse {
        return transactionalRepository.transaction {
            if (items.isEmpty()) {
                throw SelfOrderManagementException(
                    errorType = ErrorType.EMPTY_ORDER,
                    message = "Empty order",
                )
            }

            // TODO: check for available stock in stock service
            val products =
                items.flatMap {
                    val product = loadProductUseCase.getByProductNumber(it.productNumber)
                    if (!product.isLogicalItem()!!) {
                        product.components?.mapNotNull { p -> p.number }?.forEach { componentNumber ->
                            adjustInventoryUseCase.decrement(componentNumber, it.quantity)
                        }
                    }
                    MutableList(it.quantity.toInt()) { product }
                }

            var order = orderRepository.upsert(
                Order(
                    number = null,
                    orderedAt = LocalDateTime.now(),
                    customer = customerId?.let { getCustomersUseCase.findById(customerId) },
                    status = OrderStatus.CREATED,
                    items = products,
                    total = products.sumOf { it.price },
                )
            )

            val payment = providePaymentRequestUseCase.requestPayment(order)

            order = orderRepository.upsert(
                order.copy(
                    status = OrderStatus.PENDING,
                    items = order.items.map { i -> i.copy(orderNumber = order.number) },
                )
            )

            log.info("Stored order: $order")

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
                    orderRepository.upsert(order.copy(status = OrderStatus.CONFIRMED))
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
                orderRepository.upsert(order.copy(status = OrderStatus.PREPARING))
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
                orderRepository.upsert(order.copy(status = OrderStatus.COMPLETED))
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
                orderRepository.upsert(order.copy(status = OrderStatus.DONE))
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
                        // in this case, make reserved products available again
                        order.items.forEach {
                            it.number?.let { number -> adjustInventoryUseCase.increment(number, 1) }
                        }
                    }
                    orderRepository.upsert(order.copy(status = OrderStatus.CANCELLED))
                }
                ?: throw SelfOrderManagementException(
                    errorType = ErrorType.INVALID_ORDER_STATE_TRANSITION,
                    message = "This order has already been marked as completed",
                )
        }
    }
}
