package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Stock


interface StockGateway {
    fun findByComponentNumber(componentNumber: Long): Stock?

    fun update(stock: Stock): Stock
}
