package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.StockGateway
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class StockServiceTest {
    private val stockGateway = mockk<StockGateway>()

    private val stockService =
        StockService(
            stockGateway,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `should increment stock`() {
        val productNumber = 1L
        val quantity = 1L
        val quantityRequest = mapOf(productNumber to quantity)

        justRun { stockGateway.increment(quantityRequest) }
        
        stockService.incrementStockOfProducts(quantityRequest)
        
        verify(exactly = 1) { stockGateway.increment(quantityRequest) }
    }

    @Test
    fun `should decrement stock`() {
        val productNumber = 1L
        val quantity = 1L
        val quantityRequest = mapOf(productNumber to quantity)

        justRun { stockGateway.decrement(quantityRequest) }

        stockService.decrementStockOfProducts(quantityRequest)
        
        verify(exactly = 1) { stockGateway.decrement(quantityRequest) }
    }
}
