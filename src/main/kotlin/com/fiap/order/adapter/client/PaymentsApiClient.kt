package com.fiap.order.adapter.client

import com.fiap.order.driver.web.response.PaymentResponse
import com.fiap.order.driver.web.request.PaymentHTTPRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "payments-client",
    url = "\${clients.payments-api.url}"
)
interface PaymentsApiClient {

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/payments/{paymentId}"],
        consumes = ["application/json"]
    )
    fun getByPaymentId(@PathVariable paymentId: String): PaymentResponse

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/payments"],
        consumes = ["application/json"]
    )
    fun create(@RequestBody paymentHTTPRequest: PaymentHTTPRequest): PaymentResponse
}
