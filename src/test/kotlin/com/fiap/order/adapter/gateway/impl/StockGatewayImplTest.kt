package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.StockApiClient
import com.fiap.order.driver.web.request.ProductStockBatchChangeRequest
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class StockGatewayImplTest {
    private val stockApiClient = mockk<StockApiClient>()

    private val stockGatewayImpl =
        StockGatewayImpl(
            stockApiClient,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `should increment stock of products`() {
        val productNumber = 1L
        val quantity = 1L
        val productNumberQuantityMap = mapOf(productNumber to quantity)
        val request = ProductStockBatchChangeRequest(productNumberQuantityMap)
        
        justRun { stockApiClient.incrementStockOfProducts(request) }
        
        stockGatewayImpl.increment(productNumberQuantityMap)

        verify(exactly = 1) { stockApiClient.incrementStockOfProducts(request) }
    }

    @Test
    fun `should decrement stock of products`() {
        val productNumber = 1L
        val quantity = 1L
        val productNumberQuantityMap = mapOf(productNumber to quantity)
        val request = ProductStockBatchChangeRequest(productNumberQuantityMap)

        justRun { stockApiClient.decrementStockOfProducts(request) }

        stockGatewayImpl.decrement(productNumberQuantityMap)

        verify(exactly = 1) { stockApiClient.decrementStockOfProducts(request) }
    }
}
