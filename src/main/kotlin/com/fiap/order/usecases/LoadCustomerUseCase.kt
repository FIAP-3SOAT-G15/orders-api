package com.fiap.order.usecases

import com.fiap.order.domain.entities.Customer
import java.util.*

interface LoadCustomerUseCase {
    fun getByCustomerId(customerId: UUID): Customer
    
    fun findAll(): List<Customer>
    
    fun findByCustomerId(customerId: UUID): Customer?
    
    fun findByEmail(email: String): Customer?
}
