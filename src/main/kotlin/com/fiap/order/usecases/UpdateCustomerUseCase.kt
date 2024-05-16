package com.fiap.order.usecases

import com.fiap.order.domain.entities.Customer


interface UpdateCustomerUseCase {
    fun update(customer: Customer): Customer
}
