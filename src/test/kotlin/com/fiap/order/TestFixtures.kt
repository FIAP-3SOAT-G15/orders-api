package com.fiap.order

import com.fiap.order.domain.entities.Component
import com.fiap.order.domain.entities.Customer
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.OrderItem
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.entities.Stock
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.domain.valueobjects.PaymentStatus
import com.fiap.order.domain.valueobjects.ProductCategory
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
    category: ProductCategory = ProductCategory.MAIN,
    price: BigDecimal = BigDecimal("10.00"),
    description: String = "Dois hambúrgueres, alface, queijo, molho especial, cebola, picles, num pão com gergelim",
    minSub: Int = 3,
    maxSub: Int = 3,
    subitems: List<Product> = listOf(),
    components: List<Component> = listOf(),
) = Product(
    number = number,
    name = name,
    category = category,
    price = price,
    description = description,
    minSub = minSub,
    maxSub = maxSub,
    subItems = subitems,
    components = components,
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
    items: List<Product> = listOf(createProduct()),
    total: BigDecimal = BigDecimal("50.00"),
) = Order(
    number = number,
    orderedAt = orderedAt,
    customer = customer,
    status = status,
    items = items,
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