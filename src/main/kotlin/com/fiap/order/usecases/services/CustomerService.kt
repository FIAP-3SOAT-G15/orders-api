package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.domain.entities.Customer
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.usecases.CreateCustomerUseCase
import com.fiap.order.usecases.LoadCustomerUseCase
import com.fiap.order.usecases.RemoveCustomerUseCase
import com.fiap.order.usecases.SearchCustomerUseCase
import com.fiap.order.usecases.UpdateCustomerUseCase
import org.slf4j.LoggerFactory
import java.util.*

class CustomerService(
    private val customerGateway: CustomerGateway,
) : LoadCustomerUseCase,
    SearchCustomerUseCase,
    CreateCustomerUseCase,
    UpdateCustomerUseCase,
    RemoveCustomerUseCase
{
    private val log = LoggerFactory.getLogger(javaClass)
    
    override fun getByCustomerId(customerId: UUID): Customer {
        return customerGateway.findById(customerId)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.CUSTOMER_NOT_FOUND,
                message = "Customer [$customerId] not found",
            )
    }

    override fun findAll(): List<Customer> {
        return customerGateway.findAll()
    }

    override fun findByCustomerId(customerId: UUID): Customer? {
        return customerGateway.findById(customerId)
    }

    override fun searchByName(name: String): List<Customer> {
        return customerGateway.searchByName(name.trim())
    }

    override fun create(customer: Customer): Customer {
        log.info("Creating customer $customer")
        return customerGateway.create(customer.copy(id = UUID.randomUUID()))
    }

    override fun update(customer: Customer): Customer {
        log.info("Updating customer $customer")
        return customerGateway.update(customer)
    }

    override fun remove(customerId: UUID): Customer {
        log.info("Removing customer [$customerId]")
        return customerGateway.deleteById(customerId)
    }
}
