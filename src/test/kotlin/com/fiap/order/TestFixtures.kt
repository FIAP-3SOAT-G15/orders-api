package com.fiap.order

import com.fiap.order.domain.entities.Customer
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.OrderItem
import com.fiap.order.domain.entities.OrderLine
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.entities.Stock
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.domain.valueobjects.PaymentStatus
import com.fiap.order.driver.database.persistence.entities.CustomerEntity
import com.fiap.order.driver.database.persistence.entities.OrderEntity
import com.fiap.order.driver.database.persistence.entities.OrderLineEntity
import com.fiap.order.driver.web.request.CustomerRequest
import com.fiap.order.driver.web.request.OrderItemRequest
import com.fiap.order.driver.web.request.OrderRequest
import com.fiap.order.driver.web.response.PaymentResponse
import com.fiap.order.driver.web.response.PendingOrderResponse
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

fun createCustomer(
    id: UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
    document: String = "444.555.666-77",
    name: String = "Fulano de Tal",
    email: String = "fulano@detal.com",
    phone: String = "5511999999999",
    address: String = "São Paulo",
) = Customer(
    id = id,
    document = document,
    name = name,
    email = email,
    phone = phone,
    address = address,
)

fun Customer.toCustomerEntity() = CustomerEntity(
    id = id.toString(),
    document = document,
    name = name,
    email = email,
    phone = phone,
    address = address,
)

fun createCustomerRequest(
    document: String = "444.555.666-77",
    name: String = "John Doe",
    email: String = "email@johndoe.com",
    phone: String = "+5511999999999",
    address: String = "Av. Paulista, 1106, 01310-100, São Paulo"
) = CustomerRequest(
    document = document,
    name = name,
    email = email,
    phone = phone,
    address = address
)

fun createProduct(
    number: Long = 123,
    name: String = "Big Mac",
    description: String = "Dois hambúrgueres, alface, queijo, molho especial, cebola, picles, num pão com gergelim",
    price: BigDecimal = BigDecimal("10.00"),
) = Product(
    number = number,
    name = name,
    description = description,
    price = price,
)

fun createStock(
    productNumber: Long = 123,
    quantity: Long = 100,
) = Stock(
    componentNumber = productNumber,
    quantity = quantity,
)

fun createOrder(
    number: Long? = 98765,
    orderedAt: LocalDateTime = LocalDateTime.parse("2023-10-01T18:00:00"),
    customer: Customer? = null,
    status: OrderStatus = OrderStatus.CREATED,
    lines: List<OrderLine> = listOf(createOrderLine()),
    total: BigDecimal = BigDecimal("50.00"),
) = Order(
    number = number,
    orderedAt = orderedAt,
    customer = customer,
    status = status,
    lines = lines,
    total = total,
)

fun createOrderLine(
    number: Long = 1,
    orderNumber: Long = 98765,
    productNumber: Long = 123,
    name: String = "Big Mac",
    description: String = "Dois hambúrgueres, alface, queijo, molho especial, cebola, picles, num pão com gergelim",
    unitPrice: BigDecimal = BigDecimal("10.00"),
    quantity: Long = 1L,
    total: BigDecimal = BigDecimal("10.00"),
) = OrderLine(
    number = number,
    orderNumber = orderNumber,
    productNumber = productNumber,
    name = name,
    description = description,
    unitPrice = unitPrice,
    quantity = quantity,
    total = total,
)

fun Order.toOrderEntity() = OrderEntity(
    number = number ?: Random().nextLong(),
    orderedAt = orderedAt,
    customer = customer?.toCustomerEntity(),
    status = status,
    lines = lines.map { it.toOrderLineEntity() },
    total = total,
)

fun OrderLine.toOrderLineEntity() = OrderLineEntity(
    number = number!!,
    orderNumber = orderNumber!!,
    productNumber = productNumber,
    name = name,
    description = description,
    unitPrice = unitPrice,
    quantity = quantity,
    total = total,
)

fun createOrderItem(
    productNumber: Long = 123,
    quantity: Long = 1,
) = OrderItem(
    productNumber = productNumber,
    quantity = quantity,
)

fun createOrderRequest(
    items: List<OrderItemRequest> = listOf(createOrderItemRequest())
) = OrderRequest(
    items = items
)

fun createOrderItemRequest(
    productNumber: Long = 1,
    quantity: Long = 1
) = OrderItemRequest(
    productNumber = productNumber,
    quantity = quantity,
)

fun OrderItemRequest.toOrderItem() = OrderItem(
    productNumber = productNumber,
    quantity = quantity
)

fun createPaymentResponse(
    id: String = "49f285e9-f748-4f57-ad58-eb6c72cd734f",
    orderNumber: Long = 1,
    externalOrderId: String = "66b0f5f7-9997-4f49-a203-3dab2d936b50",
    externalOrderGlobalId: String? = null,
    paymentInfo: String = "00020101021243650016COM.MERCADOLIBRE...",
    createdAt: LocalDateTime = LocalDateTime.parse("2023-10-01T18:00:00"),
    status: PaymentStatus = PaymentStatus.PENDING,
    statusChangedAt: LocalDateTime = LocalDateTime.parse("2023-10-01T18:00:00"),
) = PaymentResponse(
    id = id,
    orderNumber = orderNumber,
    externalOrderId = externalOrderId,
    externalOrderGlobalId = externalOrderGlobalId,
    paymentInfo = paymentInfo,
    createdAt = createdAt,
    status = status,
    statusChangedAt = statusChangedAt,
)

fun createPendingOrderResponse(
    order: Order = createOrder(number = 1),
    paymentResponse: PaymentResponse = createPaymentResponse(orderNumber = 1)
) = PendingOrderResponse(
    order = order,
    payment = paymentResponse,
)
