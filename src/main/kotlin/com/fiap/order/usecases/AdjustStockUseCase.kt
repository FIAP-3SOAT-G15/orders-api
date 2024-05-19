package com.fiap.order.usecases

import com.fiap.order.domain.entities.Stock

interface AdjustStockUseCase {
    fun increment(componentNumber: Long, quantity: Long): Stock

    fun decrement(componentNumber: Long, quantity: Long): Stock
}
