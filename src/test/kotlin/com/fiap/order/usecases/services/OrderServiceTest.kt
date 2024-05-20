package com.fiap.order.usecases.services

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
import com.fiap.order.createCustomer
import com.fiap.order.createOrder
import com.fiap.order.createOrderItem
import com.fiap.order.createPaymentResponse
import com.fiap.order.createProduct
import com.fiap.order.createStock
import com.fiap.order.domain.valueobjects.PaymentStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.util.*

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
        every { getCustomersUseCase.getByCustomerId(any()) } returns createCustomer()
        every { getProductUseCase.getByProductNumber(any()) } returns createProduct()
        every { requestPaymentUseCase.requestPayment(any()) } returns createPaymentResponse()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    inner class GetByOrderNumber {
        @Test
        fun `should return existent order`() {
            val order = createOrder()

            every { orderRepository.findByOrderNumber(order.number!!) } returns order

            val result = orderService.getByOrderNumber(order.number!!)

            assertThat(result).isEqualTo(order)
        }

        @Test
        fun `should throw an error when trying to get non-existent order`() {
            val orderNumber = 1L

            every { orderRepository.findByOrderNumber(orderNumber) } returns null

            assertThatThrownBy { orderService.getByOrderNumber(orderNumber) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.ORDER_NOT_FOUND)
        }
    }
    
    @Test
    fun `should find all orders`() {
        val orders = listOf(createOrder())
        
        every { orderRepository.findAll() } returns orders
        
        val result = orderService.findAll()
        
        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
        verify { orderRepository.findAll() }
    }

    @Test
    fun `should find all active orders`() {
        val orders = listOf(
            createOrder(number = 1, status = OrderStatus.CONFIRMED),
            createOrder(number = 2, status = OrderStatus.PREPARING),
            createOrder(number = 3, status = OrderStatus.COMPLETED),
        )

        every { orderRepository.findAllActive() } returns orders

        val result = orderService.findAllActive()

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
        verify { orderRepository.findAllActive() }
    }
    
    @Test
    fun `should find orders by status`() {
        val orders = listOf(createOrder(status = OrderStatus.PENDING))
        val orderStatus = OrderStatus.PENDING

        every { orderRepository.findByStatus(orderStatus) } returns orders

        val result = orderService.findByStatus(orderStatus)

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
        verify { orderRepository.findByStatus(orderStatus) }
    }

    @Test
    fun `should find orders by status and customer ID`() {
        val orderStatus = OrderStatus.PENDING
        val customerId = UUID.randomUUID()
        val orders = listOf(createOrder(status = orderStatus, customer = createCustomer(id = customerId)))

        every { orderRepository.findByStatusAndCustomerId(orderStatus, customerId) } returns orders

        val result = orderService.findByStatusAndCustomerId(orderStatus, customerId)

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
        verify { orderRepository.findByStatusAndCustomerId(orderStatus, customerId) }
    }

    @Test
    fun `should find orders by customer ID`() {
        val customerId = UUID.randomUUID()
        val orders = listOf(createOrder(customer = createCustomer(id = customerId)))

        every { orderRepository.findByCustomerId(customerId) } returns orders

        val result = orderService.findByCustomerId(customerId)

        assertThat(result).containsExactlyInAnyOrderElementsOf(orders)
        verify { orderRepository.findByCustomerId(customerId) }
    }

    @Nested
    inner class CreateOrder {
        @Test
        fun `should create order`() {
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
        fun `should throw an error when order is empty`() {
            val items = emptyList<OrderItem>()

            assertThatThrownBy { orderService.create(null, items) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.EMPTY_ORDER)
        }
    }

    @Nested
    inner class ConfirmOrder {
        @Test
        fun `should confirm a order with pending status`() {
            val order = createOrder(status = OrderStatus.PENDING)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.confirmOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.CONFIRMED)
        }

        @ParameterizedTest
        @EnumSource(OrderStatus::class, names = ["PENDING"], mode = EnumSource.Mode.EXCLUDE)
        fun `should not confirm an order when status is not pending`(orderStatus: OrderStatus) {
            val order = createOrder(status = orderStatus)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(order) } answers { firstArg() }

            assertThatThrownBy { orderService.confirmOrder(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }

    @Nested
    inner class StartOrderPreparation {
        @Test
        fun `should start preparation of an order`() {
            val order = createOrder(status = OrderStatus.CONFIRMED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.startOrderPreparation(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.PREPARING)
        }

        @Test
        fun `should throw an error when trying to start preparation of an not confirmed order`() {
            val order = createOrder(status = OrderStatus.CREATED)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.startOrderPreparation(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }

    @Nested
    inner class FinishOrderPreparation {
        @Test
        fun `should finish preparation of an order`() {
            val order = createOrder(status = OrderStatus.COMPLETED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.finishOrderPreparation(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.DONE)
        }

        @Test
        fun `should throw an error when trying to finish preparation of an order not being prepared`() {
            val order = createOrder(status = OrderStatus.CREATED)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.finishOrderPreparation(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }

    @Nested
    inner class CompleteOrder {
        @Test
        fun `should complete an order`() {
            val order = createOrder(status = OrderStatus.PREPARING)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }

            val result = orderService.completeOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.COMPLETED)
        }

        @Test
        fun `should throw an error when trying to complete an order already completed`() {
            val order = createOrder(status = OrderStatus.DONE)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.completeOrder(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
                .hasMessage("Order cannot be completed until it has been prepared")
        }
    }

    @Nested
    inner class CancelOrder {
        @Test
        fun `should cancel a created order making reserved products available`() {
            val order = createOrder(status = OrderStatus.CREATED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }
            every { adjustInventoryUseCase.increment(any(), any()) } returns createStock()

            val result = orderService.cancelOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.CANCELLED)
        }

        @Test
        fun `should cancel a confirmed order making reserved products available`() {
            val order = createOrder(status = OrderStatus.CONFIRMED)

            every { orderRepository.findByOrderNumber(any()) } returns order
            every { orderRepository.upsert(any()) } answers { firstArg() }
            every { adjustInventoryUseCase.increment(any(), any()) } returns createStock()

            val result = orderService.cancelOrder(order.number!!)

            assertThat(result).isNotNull()
            assertThat(result.status).isEqualTo(OrderStatus.CANCELLED)
        }

        @Test
        fun `should throw an error when trying to cancel an order already completed`() {
            val order = createOrder(status = OrderStatus.COMPLETED)

            every { orderRepository.findByOrderNumber(any()) } returns order

            assertThatThrownBy { orderService.cancelOrder(order.number!!) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.INVALID_ORDER_STATE_TRANSITION)
        }
    }
}
