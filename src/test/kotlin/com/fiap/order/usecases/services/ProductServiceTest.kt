package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.createProduct
import com.fiap.order.domain.entities.Product
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
    fun `should return existent products`() {
        val productNumber = 1L
        val productNumbers = listOf(productNumber)
        val products = listOf(createProduct(number = productNumber))
        
        every { productGateway.findByProductNumbers(productNumbers) } returns products
        
        val result = productService.getByProductNumbers(productNumbers)
        
        assertThat(result).isEqualTo(products)
        verify(exactly = 1) { productGateway.findByProductNumbers(productNumbers) }
    }

    @Test
    fun `should throw an error when trying to get non-existent product`() {
        val productNumber = 1L
        val productNumbers = listOf(productNumber)
        val products = emptyList<Product>()

        every { productGateway.findByProductNumbers(productNumbers) } returns products

        assertThatThrownBy { productService.getByProductNumbers(productNumbers) }
            .isInstanceOf(SelfOrderManagementException::class.java)
            .hasFieldOrPropertyWithValue("errorType", ErrorType.PRODUCT_NOT_FOUND)
    }
}
