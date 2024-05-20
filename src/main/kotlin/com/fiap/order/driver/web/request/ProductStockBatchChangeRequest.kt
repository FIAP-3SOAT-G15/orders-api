package com.fiap.order.driver.web.request

data class ProductStockBatchChangeRequest (
    val productNumberQuantityMap: Map<Long, Long>
)
