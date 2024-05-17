package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.valueobjects.ProductCategory
import com.fiap.order.driver.database.persistence.entities.OrderEntity
import com.fiap.order.driver.database.persistence.entities.ProductEntity
import org.mapstruct.Condition
import org.mapstruct.Mapper

@Mapper
interface OrderMapper {
    fun toDomain(entity: OrderEntity): Order

    fun toEntity(domain: Order): OrderEntity

}
