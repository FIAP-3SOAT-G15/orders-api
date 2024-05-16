package com.fiap.order.usecases

import com.fiap.order.domain.entities.Customer


interface SearchCustomerUseCase {
    fun searchByName(name: String): List<Customer>
    fun searchByEmail(email: String): Customer?
}
