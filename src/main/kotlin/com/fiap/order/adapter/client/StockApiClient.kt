package com.fiap.order.adapter.client

import com.fiap.order.adapter.client.config.StockApiInterceptor
import com.fiap.order.domain.entities.Stock
import com.fiap.order.driver.web.request.QuantityRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "stock-client", url = "\${clients.stock-api.url}", configuration = [StockApiInterceptor::class])
interface StockApiClient {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/{componentNumber}/increment"],
        consumes = ["application/json"]
    )
    fun increment(@PathVariable("componentNumber") componentNumber: Long,
                  @RequestBody quantityRequest: QuantityRequest): Stock

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/{componentNumber}/decrement"],
        consumes = ["application/json"]
    )
    fun decrement(
        @PathVariable("componentNumber") componentNumber: Long,
        @RequestBody quantityRequest: QuantityRequest,
    ): Stock
}