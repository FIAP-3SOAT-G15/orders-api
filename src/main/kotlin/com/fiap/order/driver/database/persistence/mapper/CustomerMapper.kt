package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Customer
import com.fiap.order.driver.database.persistence.entities.CustomerEntity
import org.mapstruct.Mapper

@Mapper
interface CustomerMapper {
    fun toDomain(entity: CustomerEntity): Customer
}
