package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Stock
import com.fiap.order.driver.database.persistence.entities.StockEntity
import org.mapstruct.Mapper

@Mapper
interface StockMapper {
    fun toDomain(entity: StockEntity): Stock

    fun toEntity(domain: Stock): StockEntity
}
