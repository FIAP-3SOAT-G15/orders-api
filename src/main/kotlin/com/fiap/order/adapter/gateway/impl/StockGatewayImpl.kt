package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.StockApiClient
import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.domain.entities.Stock
import com.fiap.order.driver.web.request.ProductStockBatchChangeRequest

class StockGatewayImpl(
    private val stockApiClient: StockApiClient
) : StockGateway {

    override fun increment(productQuantityMap: Map<Long, Long>) {
        return stockApiClient.incrementStockOfProducts(ProductStockBatchChangeRequest(productQuantityMap))
    }

    override fun decrement(productQuantityMap: Map<Long, Long>) {
        return stockApiClient.decrementStockOfProducts(ProductStockBatchChangeRequest(productQuantityMap))
    }
}
