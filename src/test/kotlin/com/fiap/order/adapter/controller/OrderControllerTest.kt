package com.fiap.order.adapter.controller

import com.fiap.order.createCustomer
import com.fiap.order.createOrder
import com.fiap.order.createOrderRequest
import com.fiap.order.createPendingOrderResponse
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.toOrderItem
import com.fiap.order.usecases.ChangeOrderStatusUseCase
import com.fiap.order.usecases.CreateOrderUseCase
import com.fiap.order.usecases.LoadOrderUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class OrderControllerTest {
    private val loadOrderUseCase = mockk<LoadOrderUseCase>()
    private val createOrderUseCase = mockk<CreateOrderUseCase>()
    private val changeOrderStatusUseCase = mockk<ChangeOrderStatusUseCase>()

    private val orderController =
        OrderController(
            loadOrderUseCase,
            createOrderUseCase,
            changeOrderStatusUseCase,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should get order`() {
        val orderNumber = 1L
        val order = createOrder()

        every { loadOrderUseCase.getByOrderNumber(orderNumber) } returns order

        val result = orderController.getByOrderNumber(orderNumber)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(order)
    }

    @Test
    fun `should find all orders`() {
        val orders = listOf(createOrder())

        every { loadOrderUseCase.findAll() } returns orders

        val result = orderController.findAll()

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find all active orders`() {
        val orders = listOf(
            createOrder(status = OrderStatus.CONFIRMED),
            createOrder(status = OrderStatus.PREPARING),
            createOrder(status = OrderStatus.COMPLETED),
        )

        every { loadOrderUseCase.findAllActive() } returns orders

        val result = orderController.findAllActive()

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find orders by status`() {
        val status = OrderStatus.PENDING;
        val orders = listOf(createOrder(status = OrderStatus.PENDING))

        every { loadOrderUseCase.findByStatus(status) } returns orders

        val result = orderController.findByStatus(status.name)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find orders by status and customer ID`() {
        val status = OrderStatus.PENDING;
        val customerId = UUID.randomUUID()
        val orders = listOf(createOrder(status = OrderStatus.PENDING, customer = createCustomer(id = customerId)))

        every { loadOrderUseCase.findByStatusAndCustomerId(status, customerId) } returns orders

        val result = orderController.findByStatusAndCustomerId(status.name, customerId.toString())

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find orders by customer ID`() {
        val customerId = UUID.randomUUID()
        val orders = listOf(createOrder(customer = createCustomer(id = customerId)))

        every { loadOrderUseCase.findByCustomerId(customerId) } returns orders

        val result = orderController.findByCustomerId(customerId.toString())

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should create an order`() {
        val orderRequest = createOrderRequest()
        val items = orderRequest.items.map { it.toOrderItem() }
        val pendingOrderResponse = createPendingOrderResponse()

        every { createOrderUseCase.create(null, items) } returns pendingOrderResponse

        val result = orderController.create(orderRequest)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(pendingOrderResponse)
        verify(exactly = 1) { createOrderUseCase.create(null, items) }
    }

    @Test
    fun `should start an order`() {
        val orderNumber = 1L
        val order = createOrder(number = orderNumber, status = OrderStatus.PENDING)
        val changedOrder = order.copy(status = OrderStatus.PREPARING)

        every { changeOrderStatusUseCase.startOrderPreparation(orderNumber) } returns changedOrder
        
        val result = orderController.start(orderNumber)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(changedOrder)
        verify(exactly = 1) { changeOrderStatusUseCase.startOrderPreparation(orderNumber) }
    }

    @Test
    fun `should finish an order`() {
        val orderNumber = 1L
        val order = createOrder(number = orderNumber, status = OrderStatus.PENDING)
        val changedOrder = order.copy(status = OrderStatus.DONE)

        every { changeOrderStatusUseCase.finishOrderPreparation(orderNumber) } returns changedOrder

        val result = orderController.finish(orderNumber)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(changedOrder)
        verify(exactly = 1) { changeOrderStatusUseCase.finishOrderPreparation(orderNumber) }
    }

    @Test
    fun `should complete an order`() {
        val orderNumber = 1L
        val order = createOrder(number = orderNumber, status = OrderStatus.PENDING)
        val changedOrder = order.copy(status = OrderStatus.COMPLETED)

        every { changeOrderStatusUseCase.completeOrder(orderNumber) } returns changedOrder

        val result = orderController.complete(orderNumber)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(changedOrder)
        verify(exactly = 1) { changeOrderStatusUseCase.completeOrder(orderNumber) }
    }

    @Test
    fun `should cancel an order`() {
        val orderNumber = 1L
        val order = createOrder(number = orderNumber, status = OrderStatus.PENDING)
        val changedOrder = order.copy(status = OrderStatus.CANCELLED)

        every { changeOrderStatusUseCase.cancelOrder(orderNumber) } returns changedOrder

        val result = orderController.cancel(orderNumber)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(changedOrder)
        verify(exactly = 1) { changeOrderStatusUseCase.cancelOrder(orderNumber) }
    }

    @Test
    fun `should confirm an order`() {
        val orderNumber = 1L
        val order = createOrder(number = orderNumber, status = OrderStatus.PENDING)
        val changedOrder = order.copy(status = OrderStatus.CANCELLED)

        every { changeOrderStatusUseCase.confirmOrder(orderNumber) } returns changedOrder

        val result = orderController.confirm(orderNumber)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(changedOrder)
        verify(exactly = 1) { changeOrderStatusUseCase.confirmOrder(orderNumber) }
    }
}
