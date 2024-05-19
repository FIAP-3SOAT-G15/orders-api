package com.fiap.order.adapter.gateway.impl

import com.fiap.order.driver.database.persistence.jpa.OrderJpaRepository
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach

class OrderGatewayImplTest {
    private val orderRepository = mockk<OrderJpaRepository>()

    private val orderGatewayImpl =
        OrderGatewayImpl(
            orderRepository,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
