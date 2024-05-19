package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.createStock
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
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
        val componentNumber = 1L
        val quantity = 1L
        val stock = createStock()
        
        every { stockGateway.increment(componentNumber, quantity) } returns stock
        
        val result = stockService.increment(componentNumber, quantity)
        
        assertThat(result).isEqualTo(result)
        verify(exactly = 1) { stockGateway.increment(componentNumber, quantity) }
    }

    @Test
    fun `should decrement stock`() {
        val componentNumber = 1L
        val quantity = 1L
        val stock = createStock()

        every { stockGateway.decrement(componentNumber, quantity) } returns stock

        val result = stockService.decrement(componentNumber, quantity)

        assertThat(result).isEqualTo(result)
        verify(exactly = 1) { stockGateway.decrement(componentNumber, quantity) }
    }
}
