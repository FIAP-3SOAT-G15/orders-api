package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.domain.entities.Customer
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.usecases.*
import org.slf4j.LoggerFactory
import java.util.*

class CustomerService(
    private val repository: CustomerGateway,
) : LoadCustomerUseCase,
    SearchCustomerUseCase,
    CreateCustomerUseCase,
    UpdateCustomerUseCase,
    RemoveCustomerUseCase {
    private val log = LoggerFactory.getLogger(javaClass)
    
    override fun getById(customerId: UUID): Customer {
        return repository.findById(customerId)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.CUSTOMER_NOT_FOUND,
                message = "Customer [$customerId] not found",
            )
    }

    override fun findAll(): List<Customer> {
        return repository.findAll()
    }

    override fun findById(customerId: UUID): Customer? {
        return repository.findById(customerId)
    }

    override fun searchByName(name: String): List<Customer> {
        return repository.searchByName(name.trim())
    }

    override fun searchByEmail(email: String): Customer? {
        return repository.searchByEmail(email.trim())
    }

    override fun create(customer: Customer): Customer {
        log.info("Creating customer $customer")
        return repository.create(customer.copy(id = UUID.randomUUID()))
    }

    override fun update(customer: Customer): Customer {
        log.info("Updating customer $customer")
        return repository.update(customer)
    }

    override fun remove(customerId: UUID): Customer {
        log.info("Removing customer [$customerId]")
        return repository.deleteById(customerId)
    }
}
