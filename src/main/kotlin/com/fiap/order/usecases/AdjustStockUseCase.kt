package com.fiap.order.usecases

interface AdjustStockUseCase {
    fun incrementStockOfProducts(productQuantityMap: Map<Long, Long>)

    fun decrementStockOfProducts(productQuantityMap: Map<Long, Long>)
}
