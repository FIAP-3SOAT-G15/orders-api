package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Product
import com.fiap.order.driver.database.persistence.entities.ProductEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValuePropertyMappingStrategy

@Mapper
interface ProductMapper {
    @Mapping(
        source = "subItems",
        target = "subItems",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
    )
    fun toDomain(entity: ProductEntity): Product
}
