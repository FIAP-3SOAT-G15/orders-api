package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.domain.entities.Stock
import com.fiap.order.usecases.AdjustStockUseCase
import org.slf4j.LoggerFactory

class StockService(
    private val stockGateway: StockGateway,
) :  AdjustStockUseCase
{
    private val log = LoggerFactory.getLogger(javaClass)

    override fun increment(componentNumber: Long, quantity: Long): Stock {
        log.info("Requesting stock of product component [$componentNumber] to be incremented")
        return stockGateway.increment(componentNumber, quantity)
    }

    override fun decrement(componentNumber: Long, quantity: Long): Stock {
        log.info("Requesting stock of product component [$componentNumber] to be decremented")
        return  stockGateway.decrement(componentNumber, quantity)
    }
}
