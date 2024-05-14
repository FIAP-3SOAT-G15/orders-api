package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.ProductGateway
import com.fiap.order.domain.entities.Product
import com.fiap.order.driver.database.persistence.jpa.ProductJpaRepository
import com.fiap.order.driver.database.persistence.mapper.ProductMapper
import org.mapstruct.factory.Mappers

class ProductGatewayImpl(
    private val productJpaRepository: ProductJpaRepository,
) : ProductGateway {
    private val mapper: ProductMapper = Mappers.getMapper(ProductMapper::class.java)

    override fun findByProductNumber(productNumber: Long): Product? {
        return productJpaRepository.findById(productNumber)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }
}
