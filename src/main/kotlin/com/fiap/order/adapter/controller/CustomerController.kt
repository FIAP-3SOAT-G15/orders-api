package com.fiap.order.adapter.controller

import com.fiap.order.domain.entities.Customer
import com.fiap.order.driver.web.CustomersAPI
import com.fiap.order.driver.web.request.CustomerRequest
import com.fiap.order.usecases.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CustomerController(
    private val loadCustomersUseCase: LoadCustomerUseCase,
    private val searchCustomerUseCase: SearchCustomerUseCase,
    private val createCustomerUseCase: CreateCustomerUseCase,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    private val removeCustomerUseCase: RemoveCustomerUseCase,
) : CustomersAPI {
    override fun getById(customerId: String): ResponseEntity<Customer?> {
        customerId
            .runCatching { UUID.fromString(this) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .run { return ResponseEntity.ok(loadCustomersUseCase.getById(this)) }
    }

    override fun findAll(): ResponseEntity<List<Customer>> {
        return ResponseEntity.ok(loadCustomersUseCase.findAll())
    }

    override fun searchByName(name: String): ResponseEntity<List<Customer>> {
        return ResponseEntity.ok(searchCustomerUseCase.searchByName(name))
    }

    override fun create(customerRequest: CustomerRequest): ResponseEntity<Customer> {
        return ResponseEntity.ok(createCustomerUseCase.create(customerRequest.toDomain()))
    }

    override fun update(
        customerId: String,
        customerRequest: CustomerRequest,
    ): ResponseEntity<Customer> {
        customerId
            .runCatching { UUID.fromString(customerId) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .let { customerRequest.toDomain().copy(id = it) }
            .run { return ResponseEntity.ok(updateCustomerUseCase.update(this)) }
    }

    override fun remove(customerId: String): ResponseEntity<Customer> {
        customerId
            .runCatching { UUID.fromString(this) }
            .getOrElse { return ResponseEntity.notFound().build() }
            .run { return ResponseEntity.ok(removeCustomerUseCase.remove(this)) }
    }
}
