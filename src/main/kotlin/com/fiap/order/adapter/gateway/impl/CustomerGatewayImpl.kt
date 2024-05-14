package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.CustomerGateway
import com.fiap.order.domain.entities.Customer
import com.fiap.order.driver.database.persistence.jpa.CustomerJpaRepository
import com.fiap.order.driver.database.persistence.mapper.CustomerMapper
import org.mapstruct.factory.Mappers
import java.util.*

class CustomerGatewayImpl(
    private val customerJpaRepository: CustomerJpaRepository,
) : CustomerGateway {
    private val mapper: CustomerMapper = Mappers.getMapper(CustomerMapper::class.java)

    override fun findById(customerId: UUID): Customer {
        return customerJpaRepository.findById(customerId.toString())
            .map(mapper::toDomain)
            .orElse(null)
    }

    override fun searchByEmail(email: String): Customer {
        return customerJpaRepository.findByEmail(email)
            .map(mapper::toDomain)
            .orElse(null)
    }

    override fun searchByDocument(document: String): Customer {
        return customerJpaRepository.findByDocument(document)
            .map(mapper::toDomain)
            .orElse(null)
    }
}
