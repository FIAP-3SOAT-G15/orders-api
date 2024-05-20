package com.fiap.order.adapter.gateway.impl

import com.fiap.order.driver.database.persistence.jpa.CustomerJpaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.*

class CustomerGatewayImplTest {
    private val customerRepository = mockk<CustomerJpaRepository>()

    private val customerGatewayImpl =
        CustomerGatewayImpl(
            customerRepository,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
