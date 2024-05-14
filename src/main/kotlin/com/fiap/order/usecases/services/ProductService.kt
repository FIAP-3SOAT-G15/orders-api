package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.usecases.LoadProductUseCase

class ProductService(
    private val productRepository: ProductGateway,
) : LoadProductUseCase {
    override fun getByProductNumber(productNumber: Long): Product {
        return productRepository.findByProductNumber(productNumber)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.PRODUCT_NOT_FOUND,
                message = "Product [$productNumber] not found",
            )
    }
}
