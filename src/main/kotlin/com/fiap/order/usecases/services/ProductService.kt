package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.usecases.LoadProductUseCase
import org.slf4j.LoggerFactory

class ProductService(
    private val productGateway: ProductGateway,
) : LoadProductUseCase {
    private val log = LoggerFactory.getLogger(javaClass)
    
    override fun getByProductNumber(productNumber: Long): Product {
        log.info("Requesting product [$productNumber]")
        return productGateway.findByProductNumber(productNumber)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.PRODUCT_NOT_FOUND,
                message = "Product [$productNumber] not found",
            )
    }
}
