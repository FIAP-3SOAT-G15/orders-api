package com.fiap.order.driver.web.request

import com.fiap.order.domain.entities.OrderItem
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

data class OrderRequest(
    @ArraySchema(
        schema = Schema(implementation = OrderItemRequest::class, required = true),
        minItems = 1,
    )
    val items: List<OrderItemRequest>,
) {
    fun toOrderItemsDomain() = items.map { OrderItem(productNumber = it.productNumber, quantity = it.quantity) }
}
