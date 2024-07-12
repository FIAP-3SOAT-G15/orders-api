package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.createOrder
import com.fiap.order.createPaymentResponse
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class PaymentServiceTest {
    private val paymentGateway = mockk<PaymentGateway>()

    private val paymentService =
        PaymentService(
            paymentGateway,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
    
    @Test
    fun `should request payment`() {
        val order = createOrder(number = 1L)

        justRun { paymentGateway.notifyRequestPayment(order) }
        
        paymentService.requestPayment(order)
        
        verify(exactly = 1) { paymentGateway.notifyRequestPayment(order) }
    }
}
