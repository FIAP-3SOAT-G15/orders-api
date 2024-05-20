package com.fiap.order.adapter.client

import com.fiap.order.adapter.client.config.StockApiInterceptor
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.entities.Stock
import com.fiap.order.driver.web.request.ProductStockBatchChangeRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "stock-client",
    url = "\${clients.stock-api.url}",
    configuration = [StockApiInterceptor::class]
)
interface StockApiClient {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/admin/products/batch/increment"],
        consumes = ["application/json"]
    )
    fun incrementStockOfProducts(
        @RequestBody productStockBatchChangeRequest: ProductStockBatchChangeRequest
    )

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/admin/products/batch/decrement"],
        consumes = ["application/json"]
    )
    fun decrementStockOfProducts(
        @RequestBody productStockBatchChangeRequest: ProductStockBatchChangeRequest,
    )

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/admin/products/batch"],
        consumes = ["application/json"]
    )
    fun getByProductNumbers(
        @RequestParam("numbers") productNumbers: List<Long>
    ): List<Product>
}
