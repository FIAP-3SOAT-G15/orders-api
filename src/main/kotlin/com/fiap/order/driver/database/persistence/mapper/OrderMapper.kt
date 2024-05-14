package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.database.persistence.entities.OrderEntity
import org.mapstruct.Mapper

@Mapper
interface OrderMapper {
    fun toDomain(entity: OrderEntity): Order

    fun toEntity(domain: Order): OrderEntity
}
