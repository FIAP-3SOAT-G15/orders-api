package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Product

interface ProductGateway {
    fun findByProductNumbers(productNumbers: List<Long>): List<Product>
}
