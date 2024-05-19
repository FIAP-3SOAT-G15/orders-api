package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Stock

interface StockGateway {
    fun increment(componentNumber: Long, quantity: Long): Stock

    fun decrement(componentNumber: Long, quantity: Long): Stock
}
