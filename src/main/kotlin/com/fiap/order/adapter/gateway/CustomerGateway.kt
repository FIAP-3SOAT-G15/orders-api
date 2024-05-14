package com.fiap.order.adapter.gateway

import com.fiap.order.domain.entities.Customer
import java.util.*

interface CustomerGateway {

    fun findById(customerId: UUID): Customer?

    fun searchByEmail(email: String): Customer?

    fun searchByDocument(document: String): Customer?
}
