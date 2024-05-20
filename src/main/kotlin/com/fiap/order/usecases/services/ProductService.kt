package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.usecases.LoadProductUseCase
import org.slf4j.LoggerFactory

class ProductService(
    private val productGateway: ProductGateway,
) : LoadProductUseCase
{
    private val log = LoggerFactory.getLogger(javaClass)

    override fun getByProductNumbers(productNumbers: List<Long>): List<Product> {
        val setOfProductNumbers = productNumbers.toSet()
        
        log.info("Requesting products: $productNumbers")
        val fetchedProducts = productGateway.findByProductNumbers(setOfProductNumbers.toList())
        val setOfFetchedProductIds = fetchedProducts.map { it.number }.toSet()
        
        if (setOfProductNumbers.minus(setOfFetchedProductIds).isNotEmpty()) {
            throw SelfOrderManagementException(
                errorType = ErrorType.PRODUCT_NOT_FOUND,
                message = "Product(s) not found: ${setOfProductNumbers.minus(setOfFetchedProductIds)}",
            )
        }
        return fetchedProducts
    }
}
