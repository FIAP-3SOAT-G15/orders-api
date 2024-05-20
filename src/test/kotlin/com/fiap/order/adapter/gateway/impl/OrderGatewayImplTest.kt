package com.fiap.order.adapter.gateway.impl

import com.fiap.order.createCustomer
import com.fiap.order.createOrder
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.database.persistence.jpa.OrderJpaRepository
import com.fiap.order.toOrderEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class OrderGatewayImplTest {
    private val orderRepository = mockk<OrderJpaRepository>()

    private val orderGatewayImpl =
        OrderGatewayImpl(
            orderRepository,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should find all orders`() {
        val orders = listOf(createOrder())
        val orderEntities = orders.map { it.toOrderEntity() }

        every { orderRepository.findAll() } returns orderEntities

        val result = orderGatewayImpl.findAll()

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find all active orders`() {
        val orders = listOf(
            createOrder(status = OrderStatus.CONFIRMED),
            createOrder(status = OrderStatus.PREPARING),
            createOrder(status = OrderStatus.COMPLETED),
        )
        val orderEntities = orders.map { it.toOrderEntity() }

        every { orderRepository.findAll() } returns orderEntities

        val result = orderGatewayImpl.findAll()

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find order by number`() {
        val orderNumber = 1L
        val order = createOrder(number = orderNumber)
        val orderEntity = order.toOrderEntity()

        every { orderRepository.findById(orderNumber) } returns Optional.of(orderEntity)

        val result = orderGatewayImpl.findByOrderNumber(orderNumber)

        assertThat(result).isEqualTo(order)
    }

    @Test
    fun `should find orders by status`() {
        val status = OrderStatus.CONFIRMED
        val orders = listOf(createOrder(status = status))
        val orderEntities = orders.map { it.toOrderEntity() }

        every { orderRepository.findByStatus(status) } returns orderEntities

        val result = orderGatewayImpl.findByStatus(status)

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find orders by status and customer ID`() {
        val status = OrderStatus.CONFIRMED
        val customerId = UUID.randomUUID()
        val orders = listOf(createOrder(status = status, customer = createCustomer(id = customerId)))
        val orderEntities = orders.map { it.toOrderEntity() }

        every { orderRepository.findByStatusAndCustomerId(status, customerId.toString()) } returns orderEntities

        val result = orderGatewayImpl.findByStatusAndCustomerId(status, customerId)

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Test
    fun `should find orders by customer ID`() {
        val customerId = UUID.randomUUID()
        val orders = listOf(createOrder(customer = createCustomer(id = customerId)))
        val orderEntities = orders.map { it.toOrderEntity() }

        every { orderRepository.findByCustomerId(customerId.toString()) } returns orderEntities

        val result = orderGatewayImpl.findByCustomerId(customerId)

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
    }

    @Nested
    inner class UpsertOrder {

        @Test
        fun `should insert new order`() {
            val order = createOrder(number = 1)
            val orderEntity = order.toOrderEntity()

            every { orderRepository.findById(order.number!!) } returns Optional.empty()
            every { orderRepository.save(any()) } returns orderEntity

            val result = orderGatewayImpl.upsert(order)

            assertThat(result).isEqualTo(order)
            verify(exactly = 1) { orderRepository.save(any()) }
        }

        @Test
        fun `should update order`() {
            val oldOrder = createOrder(number = 1L, status = OrderStatus.PENDING)
            val newOrder = oldOrder.copy(status = OrderStatus.CONFIRMED)
            val oldOrderEntity = oldOrder.toOrderEntity()
            val newOrderEntity = newOrder.toOrderEntity()

            every { orderRepository.findById(newOrder.number!!) } returns Optional.of(oldOrderEntity)
            every { orderRepository.save(any()) } returns newOrderEntity

            val result = orderGatewayImpl.upsert(newOrder)

            assertThat(result).isEqualTo(newOrder)
            verify(exactly = 1) { orderRepository.save(any()) }
        }
    }
}
