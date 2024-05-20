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

    override fun incrementStockOfProducts(productQuantityMap: Map<Long, Long>) {
        log.info("Requesting increment of stock for product-quantity map: $productQuantityMap")
        stockGateway.increment(productQuantityMap)
    }

    override fun decrementStockOfProducts(productQuantityMap: Map<Long, Long>) {
        log.info("Requesting decrement of stock for product-quantity map: $productQuantityMap")
        stockGateway.decrement(productQuantityMap)
    }
}
