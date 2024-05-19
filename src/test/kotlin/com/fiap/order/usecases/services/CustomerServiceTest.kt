package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.createCustomer
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class CustomerServiceTest {
    private val customerGateway = mockk<CustomerGateway>()

    private val customerService =
        CustomerService(
            customerGateway,
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    inner class GetByCustomerId {

        @Test
        fun `should get existent customer`() {
            val customerId = UUID.randomUUID()
            val customer = createCustomer(id = customerId)

            every { customerGateway.findById(customerId) } returns customer

            val result = customerService.getByCustomerId(customerId)

            assertThat(result).isEqualTo(customer)
            verify(exactly = 1) { customerGateway.findById(customerId) }
        }

        @Test
        fun `should throw an error when trying to get non-existent customer`() {
            val customerId = UUID.randomUUID()
            
            every { customerGateway.findById(customerId) } returns null

            assertThatThrownBy { customerService.getByCustomerId(customerId) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CUSTOMER_NOT_FOUND)
        }
    }
    
    @Test
    fun `should find all customers`() {
        val customers = listOf(createCustomer())

        every { customerGateway.findAll() } returns customers

        val result = customerGateway.findAll()
        
        assertThat(result).containsExactlyInAnyOrderElementsOf(customers)
    }

    @Test
    fun `should search customers by name`() {
        val customerName = "John Doe"
        val customers = listOf(createCustomer(name = customerName))

        every { customerGateway.searchByName(customerName) } returns customers

        val result = customerGateway.searchByName(customerName)
        
        assertThat(result).containsExactlyInAnyOrderElementsOf(customers)
    }

    @Test
    fun `should create a customer`() {
        val customer = createCustomer()

        every { customerGateway.create(customer) } returns customer

        val result = customerGateway.create(customer)
        
        assertThat(result).isEqualTo(customer)
        verify(exactly = 1) { customerGateway.create(customer) }
    }

    @Test
    fun `should update a customer`() {
        val customer = createCustomer()

        every { customerGateway.update(customer) } returns customer

        val result = customerGateway.update(customer)

        assertThat(result).isEqualTo(customer)
        verify(exactly = 1) { customerGateway.update(customer) }
    }

    @Test
    fun `should remove a customer`() {
        val customerId = UUID.randomUUID()
        val customer = createCustomer()

        every { customerGateway.deleteById(customerId) } returns customer

        val result = customerGateway.deleteById(customerId)

        assertThat(result).isEqualTo(customer)
        verify(exactly = 1) { customerGateway.deleteById(customerId) }
    }
}
