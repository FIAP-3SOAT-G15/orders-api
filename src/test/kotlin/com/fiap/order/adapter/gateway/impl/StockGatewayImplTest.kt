package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.StockApiClient
import com.fiap.order.createStock
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
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
    fun `should increment stock`() {
        val componentNumber = 1L
        val quantity = 1L
        val stock = createStock()
        
        every { stockApiClient.increment(componentNumber, any()) } returns stock
        
        val result = stockGatewayImpl.increment(componentNumber, quantity)
        
        assertThat(result).isEqualTo(stock)
    }

    @Test
    fun `should decrement stock`() {
        val componentNumber = 1L
        val quantity = 1L
        val stock = createStock()

        every { stockApiClient.decrement(componentNumber, any()) } returns stock

        val result = stockGatewayImpl.decrement(componentNumber, quantity)

        assertThat(result).isEqualTo(stock)
    }
}
