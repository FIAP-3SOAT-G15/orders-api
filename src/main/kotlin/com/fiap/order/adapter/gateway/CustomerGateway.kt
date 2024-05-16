package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Customer
import java.util.*

interface CustomerGateway {

    fun findAll(): List<Customer>

    fun findById(customerId: UUID): Customer?

    fun searchByName(name: String): List<Customer>

    fun searchByEmail(email: String): Customer?

    fun searchByDocument(document: String): Customer?

    fun create(customer: Customer): Customer

    fun update(customer: Customer): Customer

    fun deleteById(customerId: UUID): Customer

    fun deleteAll()
}
