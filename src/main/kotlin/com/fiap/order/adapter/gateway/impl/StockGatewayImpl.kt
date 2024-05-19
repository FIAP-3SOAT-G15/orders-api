package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.StockApiClient
import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.domain.entities.Stock
import com.fiap.order.driver.web.request.QuantityRequest

class StockGatewayImpl(
    private val stockApiClient: StockApiClient
) : StockGateway {

    override fun increment(componentNumber: Long, quantity: Long): Stock {
        return stockApiClient.increment(componentNumber, QuantityRequest(quantity))
    }

    override fun decrement(componentNumber: Long, quantity: Long): Stock {
        return stockApiClient.decrement(componentNumber, QuantityRequest(quantity));
    }
}
