package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.StockApiClient
import com.fiap.order.createProduct
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ProductGatewayImplTest {
    private val stockApiClient = mockk<StockApiClient>()

    private val productGatewayImpl =
        ProductGatewayImpl(
            stockApiClient,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `should find product by number`() {
        val productNumber = 1L
        val product = createProduct(number = productNumber)
        
        every { stockApiClient.getByProductNumber(productNumber) } returns product
        
        val result = productGatewayImpl.findByProductNumber(productNumber)
        
        assertThat(result).isEqualTo(product)
    }
}
