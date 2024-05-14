package com.fiap.order.usecases.services

import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.domain.entities.Customer
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.usecases.LoadCustomerUseCase
import java.util.*

class CustomerService(
    private val repository: CustomerGateway,
) : LoadCustomerUseCase {
    override fun getById(customerId: UUID): Customer {
        return repository.findById(customerId)
            ?: throw SelfOrderManagementException(
                errorType = ErrorType.CUSTOMER_NOT_FOUND,
                message = "Customer [$customerId] not found",
            )
    }

    override fun findById(customerId: UUID): Customer? {
        return repository.findById(customerId)
    }
}
