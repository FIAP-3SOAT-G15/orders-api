package com.fiap.order.usecases

import com.fiap.order.domain.entities.Product

interface LoadProductUseCase {
    fun getByProductNumber(productNumber: Long): Product
}
