package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.domain.entities.Stock
import com.fiap.order.usecases.AdjustStockUseCase


class StockService(
    private val stockGateway: StockGateway,
) :  AdjustStockUseCase {

    override fun increment(
        componentNumber: Long,
        quantity: Long,
    ): Stock {
        return stockGateway.increment(componentNumber, quantity)
    }

    override fun decrement(
        componentNumber: Long,
        quantity: Long,
    ): Stock {
        return  stockGateway.decrement(componentNumber, quantity)

    }


}
