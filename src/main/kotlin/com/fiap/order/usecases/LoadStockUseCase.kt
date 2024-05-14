package com.fiap.order.usecases

import com.fiap.order.domain.entities.Stock


interface LoadStockUseCase {
    fun getByComponentNumber(componentNumber: Long): Stock
}
