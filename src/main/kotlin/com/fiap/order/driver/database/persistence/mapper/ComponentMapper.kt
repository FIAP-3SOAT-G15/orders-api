package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Component
import com.fiap.order.driver.database.persistence.entities.ComponentEntity
import org.mapstruct.Mapper

@Mapper
interface ComponentMapper {
    fun toDomain(entity: ComponentEntity): Component
}
