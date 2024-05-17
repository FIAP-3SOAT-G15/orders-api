package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Product
import com.fiap.order.domain.valueobjects.ProductCategory
import com.fiap.order.driver.database.persistence.entities.ProductEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValuePropertyMappingStrategy

@Mapper
interface ProductMapper {

    fun toDomain(entity: ProductEntity): Product
}
