package com.fiap.order.adapter.client

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.Payment
import com.fiap.order.driver.web.request.PaymentRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "payments-client", url = "\${clients.payments-api.url}")
interface PaymentsApiClient {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/payments/{orderNumber}"],
        consumes = ["application/json"]
    )
    fun getByOrderNumber(@PathVariable orderNumber: Long): Payment

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/payments/notifications/{orderNumber}"],
        consumes = ["application/json"]
    )
    fun notify(
        @PathVariable orderNumber: Long,
        @RequestParam(value = "id") resourceId: String,
        @RequestParam topic: String
    ): Any

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/payments/create"],
        consumes = ["application/json"]
    )
    fun create(@RequestBody order: Order): PaymentRequest
}
