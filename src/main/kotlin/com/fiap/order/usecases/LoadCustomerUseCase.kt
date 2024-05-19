package com.fiap.order.usecases

import com.fiap.order.domain.entities.Customer
import java.util.*

interface LoadCustomerUseCase {
    fun getById(customerId: UUID): Customer
    
    fun findAll(): List<Customer>
    
    fun findById(customerId: UUID): Customer?
}
