package com.fiap.order.adapter.gateway.impl

import com.fiap.order.createCustomer
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.driver.database.persistence.jpa.CustomerJpaRepository
import com.fiap.order.toCustomerEntity
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
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

    @Test
    fun `should find all customers`() {
        val customers = listOf(createCustomer())
        val customerEntities = customers.map { it.toCustomerEntity() }

        every { customerRepository.findAll() } returns customerEntities

        val result = customerGatewayImpl.findAll()

        assertThat(result).containsExactlyInAnyOrderElementsOf(customers)
    }
    
    @Test
    fun `should find customer by ID`() {
        val customerId = UUID.randomUUID()
        val customer = createCustomer(id = customerId)
        val customerEntity = customer.toCustomerEntity()
        
        every { customerRepository.findById(customerId.toString()) } returns Optional.of(customerEntity)
        
        val result = customerGatewayImpl.findById(customerId)
        
        assertThat(result).isEqualTo(customer)
    }


    @Test
    fun `should find customer by email`() {
        val email = "email@johndoe.com"
        val customer = createCustomer(email = email)
        val customerEntity = customer.toCustomerEntity()

        every { customerRepository.findByEmail(email) } returns Optional.of(customerEntity)

        val result = customerGatewayImpl.findByEmail(email)

        assertThat(result).isEqualTo(customer)
    }

    @Test
    fun `should search customers by name`() {
        val name = "John Doe"
        val customers = listOf(createCustomer(name = name))
        val customerEntities = customers.map { it.toCustomerEntity() }

        every { customerRepository.findByNameContainingIgnoreCase(name) } returns customerEntities

        val result = customerGatewayImpl.searchByName(name)

        assertThat(result).isEqualTo(customers)
    }

    @Nested
    inner class CreateCustomer {

        @Test
        fun `should insert new customer`() {
            val customer = createCustomer(email = "email@johndoe.com", document = "444.555.666-77")
            val customerEntity = customer.toCustomerEntity()

            every { customerRepository.findByEmail(customer.email!!) } returns Optional.empty()
            every { customerRepository.findByDocument(customer.document!!) } returns Optional.empty()
            every { customerRepository.save(any()) } returns customerEntity

            val result = customerGatewayImpl.create(customer)

            assertThat(result).isEqualTo(customer)
            verify(exactly = 1) { customerRepository.save(any()) }
        }

        @Test
        fun `should not insert customer on conflicting with email`() {
            val customer = createCustomer(email = "email@johndoe.com")
            val customerEntity = customer.toCustomerEntity()

            every { customerRepository.findByEmail(customer.email!!) } returns Optional.of(customerEntity)

            assertThatThrownBy { customerGatewayImpl.create(customer) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CUSTOMER_ALREADY_EXISTS)
        }

        @Test
        fun `should not insert customer on conflicting with document`() {
            val customer = createCustomer(document = "444.555.666-77")
            val customerEntity = customer.toCustomerEntity()

            every { customerRepository.findByEmail(customer.email!!) } returns Optional.empty()
            every { customerRepository.findByEmail(customer.email!!) } returns Optional.of(customerEntity)

            assertThatThrownBy { customerGatewayImpl.create(customer) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CUSTOMER_ALREADY_EXISTS)
        }
    }

    @Nested
    inner class UpdateCustomer {

        @Test
        fun `should update customer`() {
            val oldCustomer = createCustomer(phone = "+5511999999999")
            val newCustomer = oldCustomer.copy(phone = "+5511988888888")
            val oldCustomerEntity = oldCustomer.toCustomerEntity()
            val newCustomerEntity = newCustomer.toCustomerEntity()

            every { customerRepository.findById(newCustomer.id.toString()) } returns Optional.of(oldCustomerEntity)
            every { customerRepository.save(any()) } returns newCustomerEntity

            val result = customerGatewayImpl.update(newCustomer)

            assertThat(result).isEqualTo(newCustomer)
            verify(exactly = 1) { customerRepository.save(any()) }
        }

        @Test
        fun `should thrown an error when trying to update an non-existent customer`() {
            val customer = createCustomer(phone = "+5511999999999")

            every { customerRepository.findById(customer.id.toString()) } returns Optional.empty()

            assertThatThrownBy { customerGatewayImpl.update(customer) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CUSTOMER_NOT_FOUND)
        }
    }

    @Nested
    inner class DeleteCustomer {

        @Test
        fun `should delete customer`() {
            val customerId = UUID.randomUUID()
            val customer = createCustomer(id = customerId)
            val customerEntity = customer.toCustomerEntity()

            every { customerRepository.findById(customerId.toString()) } returns Optional.of(customerEntity)
            justRun { customerRepository.deleteById(customerId.toString()) }

            val result = customerGatewayImpl.deleteById(customerId)

            assertThat(result).isEqualTo(customer)
            verify(exactly = 1) { customerRepository.deleteById(customerId.toString()) }
        }

        @Test
        fun `should thrown an error when trying to delete an non-existent customer`() {
            val customerId = UUID.randomUUID()
            val customer = createCustomer(id = customerId)

            every { customerRepository.findById(customer.id.toString()) } returns Optional.empty()

            assertThatThrownBy { customerGatewayImpl.deleteById(customerId) }
                .isInstanceOf(SelfOrderManagementException::class.java)
                .hasFieldOrPropertyWithValue("errorType", ErrorType.CUSTOMER_NOT_FOUND)
        }
    }
}
