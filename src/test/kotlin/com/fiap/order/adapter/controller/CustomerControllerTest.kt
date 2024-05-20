package com.fiap.order.adapter.controller

import com.fiap.order.createCustomer
import com.fiap.order.createCustomerRequest
import com.fiap.order.usecases.CreateCustomerUseCase
import com.fiap.order.usecases.LoadCustomerUseCase
import com.fiap.order.usecases.RemoveCustomerUseCase
import com.fiap.order.usecases.SearchCustomerUseCase
import com.fiap.order.usecases.UpdateCustomerUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class CustomerControllerTest {
    private val loadCustomerUseCase = mockk<LoadCustomerUseCase>()
    private val searchCustomerUseCase = mockk<SearchCustomerUseCase>()
    private val createCustomerUseCase = mockk<CreateCustomerUseCase>()
    private val updateCustomerUseCase = mockk<UpdateCustomerUseCase>()
    private val removeCustomerUseCase = mockk<RemoveCustomerUseCase>()
    
    private val customerController =
        CustomerController(
            loadCustomerUseCase,
            searchCustomerUseCase,
            createCustomerUseCase,
            updateCustomerUseCase,
            removeCustomerUseCase
        )

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should get customer`() {
        val customerId = UUID.randomUUID()
        val customer = createCustomer()
        
        every { loadCustomerUseCase.getByCustomerId(customerId) } returns customer
        
        val result = customerController.getById(customerId.toString())
        
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(customer)
    }
    
    @Test
    fun `should find all customers`() {
        val customers = listOf(createCustomer())
        
        every { loadCustomerUseCase.findAll() } returns customers
        
        val result = customerController.findAll()
        
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(customers)
    }

    @Test
    fun `should search customers by name`() {
        val customerName = "John Doe"
        val customers = listOf(createCustomer(name = customerName))
        
        every { searchCustomerUseCase.searchByName(customerName) } returns customers
        
        val result = customerController.searchByName(customerName)
        
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).containsExactlyInAnyOrderElementsOf(customers)
    }

    @Test
    fun `should create a customer`() {
        val customerRequest = createCustomerRequest()
        val customer = customerRequest.toDomain()
        
        every { createCustomerUseCase.create(any()) } returns customer
        
        val result = customerController.create(customerRequest)
        
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(customer)
    }

    @Test
    fun `should update a customer`() {
        val customerId = UUID.randomUUID()
        val customerRequest = createCustomerRequest()
        val customer = customerRequest.toDomain().copy(id = customerId)
        
        every { updateCustomerUseCase.update(any()) } returns customer
        
        val result = customerController.update(customerId.toString(), customerRequest)
        
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(customer)
    }

    @Test
    fun `should remove a customer`() {
        val customerId = UUID.randomUUID()
        val customerRequest = createCustomerRequest()
        val customer = customerRequest.toDomain()
        
        every { removeCustomerUseCase.remove(any()) } returns customer
        
        val result = customerController.remove(customerId.toString())
        
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(customer)
    }
}
