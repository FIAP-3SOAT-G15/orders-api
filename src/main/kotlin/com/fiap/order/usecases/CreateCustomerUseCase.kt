package com.fiap.order.usecases

import com.fiap.order.domain.entities.Customer

interface CreateCustomerUseCase {
    fun create(customer: Customer): Customer
}
