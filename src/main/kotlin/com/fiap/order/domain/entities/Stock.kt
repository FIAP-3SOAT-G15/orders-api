package com.fiap.order.domain.entities

data class Stock(
    val componentNumber: Long,
    val quantity: Long,
) {
    fun update(newStock: Stock): Stock =
        copy(
            quantity = newStock.quantity,
        )
}
