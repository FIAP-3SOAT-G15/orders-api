package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.StockGateway
import com.fiap.order.domain.entities.Stock
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.driver.database.persistence.jpa.StockJpaRepository
import com.fiap.order.driver.database.persistence.mapper.StockMapper
import org.mapstruct.factory.Mappers

class StockGatewayImpl(
    private val stockJpaRepository: StockJpaRepository,
) : StockGateway {
    private val mapper = Mappers.getMapper(StockMapper::class.java)

    override fun findByComponentNumber(componentNumber: Long): Stock? {
        return stockJpaRepository.findById(componentNumber)
            .map(mapper::toDomain)
            .orElse(null)
    }

    override fun update(stock: Stock): Stock {
        val newItem =
            findByComponentNumber(stock.componentNumber)
                ?.update(stock)
                ?: throw SelfOrderManagementException(
                    errorType = ErrorType.STOCK_NOT_FOUND,
                    message = "Stock [${stock.componentNumber}] not found",
                )
        return persist(newItem)
    }

    private fun persist(stock: Stock): Stock =
        stock
            .let(mapper::toEntity)
            .let(stockJpaRepository::save)
            .let(mapper::toDomain)
}
