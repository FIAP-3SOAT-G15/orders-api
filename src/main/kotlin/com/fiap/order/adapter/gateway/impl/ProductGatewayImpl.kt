package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.StockApiClient
import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.domain.entities.Product

class ProductGatewayImpl(
    private val stockApiClient: StockApiClient,
) : ProductGateway {

    override fun findByProductNumber(productNumber: Long): Product {
        return stockApiClient.getByProductNumber(productNumber)
    }
}
