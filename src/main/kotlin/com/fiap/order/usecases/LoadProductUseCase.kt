package com.fiap.order.usecases

import com.fiap.order.domain.entities.Product

interface LoadProductUseCase {
    fun getByProductNumbers(productNumbers: List<Long>): List<Product>
}
