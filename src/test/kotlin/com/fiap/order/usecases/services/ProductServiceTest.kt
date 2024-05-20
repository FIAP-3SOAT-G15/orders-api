package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.createProduct
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ProductServiceTest {
    private val productGateway = mockk<ProductGateway>()

    private val productService =
        ProductService(
            productGateway,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `should return existent product`() {
        val productNumber = 1L
        val product = createProduct(number = productNumber)
        
        every { productGateway.findByProductNumber(productNumber) } returns product
        
        val result = productService.getByProductNumber(productNumber)
        
        assertThat(result).isEqualTo(product)
        verify(exactly = 1) { productGateway.findByProductNumber(productNumber) }
    }

    @Test
    fun `should throw an error when trying to get non-existent product`() {
        val productNumber = 1L

        every { productGateway.findByProductNumber(productNumber) } returns null

        assertThatThrownBy { productService.getByProductNumber(productNumber) }
            .isInstanceOf(SelfOrderManagementException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.PRODUCT_NOT_FOUND)
    }
}
