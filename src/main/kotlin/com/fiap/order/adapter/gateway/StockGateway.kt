package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Stock

interface StockGateway {
    fun increment(productQuantityMap: Map<Long, Long>)

    fun decrement(productQuantityMap: Map<Long, Long>)
}
