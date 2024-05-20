package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.client.PaymentsApiClient
import com.fiap.order.createOrder
import com.fiap.order.createPaymentResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.*

class PaymentGatewayImplTest {
    private val paymentsApiClient = mockk<PaymentsApiClient>()

    private val paymentGatewayImpl =
        PaymentGatewayImpl(
            paymentsApiClient,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should find payment by payment ID`() {
        val paymentId = UUID.randomUUID().toString()
        val paymentResponse = createPaymentResponse(id = paymentId)

        every { paymentsApiClient.getByPaymentId(paymentId) } returns paymentResponse

        val result = paymentGatewayImpl.findByPaymentId(paymentId)

        assertThat(result).isEqualTo(paymentResponse)
    }
    
    @Test
    fun `should obtain payment`() {
        val order = createOrder(number = 1)
        val paymentResponse = createPaymentResponse(orderNumber = order.number!!)
        
        every { paymentsApiClient.create(any()) } returns paymentResponse
        
        val result = paymentGatewayImpl.requestPayment(order)
        
        assertThat(result).isEqualTo(paymentResponse)
    }
}
