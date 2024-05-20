package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.domain.entities.Customer
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.driver.database.persistence.jpa.CustomerJpaRepository
import com.fiap.order.driver.database.persistence.mapper.CustomerMapper
import org.mapstruct.factory.Mappers
import java.util.*

class CustomerGatewayImpl(
    private val customerRepository: CustomerJpaRepository,
) : CustomerGateway {
    private val mapper: CustomerMapper = Mappers.getMapper(CustomerMapper::class.java)

    override fun findAll(): List<Customer> =
        customerRepository.findAll().map(mapper::toDomain)

    override fun findById(customerId: UUID): Customer? =
        customerRepository.findById(customerId.toString()).map(mapper::toDomain).orElse(null)

    override fun findByEmail(email: String): Customer? =
        customerRepository.findByEmail(email).map(mapper::toDomain).orElse(null)

    override fun findByDocument(document: String): Customer? =
        customerRepository.findByDocument(document).map(mapper::toDomain).orElse(null)
    
    override fun searchByName(name: String): List<Customer> =
        customerRepository.findByNameContainingIgnoreCase(name).map(mapper::toDomain)

    override fun create(customer: Customer): Customer {
        customer.email
            ?.let { findByEmail(it) }
            ?.let {
                throw SelfOrderManagementException(
                    errorType = ErrorType.CUSTOMER_ALREADY_EXISTS,
                    message = "Customer with email [${customer.email}] already exists",
                )
            }

        customer.document
            ?.let { findByDocument(it) }
            ?.let {
                throw SelfOrderManagementException(
                    errorType = ErrorType.CUSTOMER_ALREADY_EXISTS,
                    message = "Customer with document [${customer.document}] already exists",
                )
            }

        return persist(customer)
    }

    override fun update(customer: Customer): Customer {
        val newItem =
            findById(customer.id)
                ?.update(customer)
                ?: throw SelfOrderManagementException(
                    errorType = ErrorType.CUSTOMER_NOT_FOUND,
                    message = "Customer [${customer.document}] not found",
                )
        return persist(newItem)
    }

    override fun deleteById(customerId: UUID): Customer {
        return findById(customerId)
            ?.let {
                customerRepository.deleteById(customerId.toString())
                it
            }
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.CUSTOMER_NOT_FOUND,
                message = "Customer [$customerId] not found",
            )
    }

    override fun deleteAll() {
        customerRepository.deleteAll()
    }

    private fun persist(customer: Customer): Customer =
        customer
            .let(mapper::toEntity)
            .let(customerRepository::save)
            .let(mapper::toDomain)
}
