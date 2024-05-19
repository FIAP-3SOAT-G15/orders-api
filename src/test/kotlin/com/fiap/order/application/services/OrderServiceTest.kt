package com.fiap.order.application.services

import com.fiap.order.adapter.gateway.OrderGateway
import com.fiap.order.adapter.gateway.impl.TransactionalGatewayImpl
import com.fiap.order.domain.entities.OrderItem
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.usecases.AdjustStockUseCase
import com.fiap.order.usecases.LoadCustomerUseCase
import com.fiap.order.usecases.LoadProductUseCase
import com.fiap.order.usecases.RequestPaymentUseCase
import com.fiap.order.usecases.services.OrderService
import createCustomer
import createOrder
import createOrderItem
import createPaymentResponse
import createProduct
import createStock
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

class OrderServiceTest {
    private val orderRepository = mockk<OrderGateway>()
    private val getCustomersUseCase = mockk<LoadCustomerUseCase>()
    private val getProductUseCase = mockk<LoadProductUseCase>()
    private val adjustInventoryUseCase = mockk<AdjustStockUseCase>()
    private val requestPaymentUseCase = mockk<RequestPaymentUseCase>()
    private val transactionalRepository = TransactionalGatewayImpl()

    private val orderService =
        OrderService(
            orderRepository,
            getCustomersUseCase,
            getProductUseCase,
            adjustInventoryUseCase,
            requestPaymentUseCase,
            transactionalRepository,
        )

    @BeforeEach
    fun setUp() {
        every { getCustomersUseCase.getById(any()) } returns createCustomer()
        every { getProductUseCase.getByProductNumber(any()) } returns createProduct()
        every { requestPaymentUseCase.requestPayment(any()) } returns createPaymentResponse()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    inner class GetByOrderNumberTest {
        @Test
        fun `should find existent order`() {
            val order = createOrder()

            every { orderRepository.findByOrderNumber(order.number!!) } returns order

            val result = orderService.getByOrderNumber(order.number!!)

            assertThat(result).isEqualTo(order)
        }

        @Test
        fun `should not find non-existent order`() {
            val orderNumber = 1L

            every { orderRepository.findByOrderNumber(orderNumber) } returns null

            assertThatThrownBy { orderService.getByOrderNumber(orderNumber) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND)
        }
    }

    @Nested
    inner class CreateTest {
        @Test
        fun `create create order`() {
            val items = listOf(createOrderItem())

            every { adjustInventoryUseCase.decrement(any(), any()) } returns createStock()
            every { orderRepository.upsert(any()) } returns createOrder(status = OrderStatus.CREATED)

            val result = orderService.create(null, items)

            assertThat(result).isNotNull()
            assertThat(result.order.number).isNotNull()
            assertThat(result.order.items).hasSize(1)
            assertThat(result.order.total).isEqualTo(BigDecimal("50.00"))
        }

        @Test
        fun `create should throw an exception when items are empty`() {
            val items = emptyList<OrderItem>()

            assertThatThrownBy { orderService.create(null, items) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.EMPTY_ORDER)
        }
    }

    @Nested
    inner class ConfirmOrderTest {
        @Test
        fun `should confirm a pending order with a confirmed payment`() {
            val order = createOrder(status = OrderStatus.PENDING)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.confirmOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.CONFIRMED)
        }

        @ParameterizedTest
        @EnumSource(OrderStatus::class, names = ["CREATED", "CONFIRMED", "PREPARING", "COMPLETED", "DONE", "CANCELLED"])
        fun `should not confirm an order which is not pending`(orderStatus: OrderStatus) {
            val order = createOrder(status = orderStatus)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(order) } answers { firstArg() }

            assertThatThrownBy { orderService.confirmOrder(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }

    @Nested
    inner class StartOrderPreparationTest {
        @Test
        fun `startOrderPreparation should start preparation for a CONFIRMED order`() {
            val order = createOrder(status = OrderStatus.CONFIRMED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.startOrderPreparation(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.PREPARING)
        }

        @Test
        fun `startOrderPreparation should throw an exception for a non-CONFIRMED order`() {
            val order = createOrder(status = OrderStatus.CREATED)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.startOrderPreparation(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }

    @Nested
    inner class FinishOrderPreparationTest {
        @Test
        fun `finishOrderPreparation should finish a COMPLETED order when it is delivered`() {
            val order = createOrder(status = OrderStatus.COMPLETED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.finishOrderPreparation(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.DONE)
        }

        @Test
        fun `finishOrderPreparation should throw an exception for a non-PREPARING order`() {
            val order = createOrder(status = OrderStatus.CREATED)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.finishOrderPreparation(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }

    @Nested
    inner class CompleteOrderTest {
        @Test
        fun `completeOrder should complete an order that is not yet completed (status is not DONE)`() {
            val order = createOrder(status = OrderStatus.PREPARING)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.completeOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.COMPLETED)
        }

        @Test
        fun `completeOrder should throw an exception for an already completed order (status is DONE)`() {
            val order = createOrder(status = OrderStatus.DONE)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.completeOrder(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
                .hasMessage("Order cannot be completed until it has been prepared")
        }
    }

    @Nested
    inner class CancelOrderTest {
        @Test
        fun `cancelOrder should cancel a CREATED order and make reserved products available`() {
            val order = createOrder(status = OrderStatus.CREATED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }
            every { adjustInventoryUseCase.increment(any(), any()) } returns createStock()

            val result = orderService.cancelOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.CANCELLED)
        }

        @Test
        fun `cancelOrder should cancel a CONFIRMED order and make reserved products available`() {
            val order = createOrder(status = OrderStatus.CONFIRMED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }
            every { adjustInventoryUseCase.increment(any(), any()) } returns createStock()

            val result = orderService.cancelOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.CANCELLED)
        }

        @Test
        fun `cancelOrder should throw an exception for a COMPLETED order`() {
            val order = createOrder(status = OrderStatus.COMPLETED)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.cancelOrder(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }
}
