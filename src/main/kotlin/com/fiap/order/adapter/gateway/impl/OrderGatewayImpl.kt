package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.OrderGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.database.persistence.jpa.OrderJpaRepository
import com.fiap.order.driver.database.persistence.mapper.OrderMapper
import org.mapstruct.factory.Mappers
import java.util.*

class OrderGatewayImpl(
    private val orderJpaRepository: OrderJpaRepository,
) : OrderGateway {
    private val mapper = Mappers.getMapper(OrderMapper::class.java)

    override fun findAllActiveOrders(): List<Order> {
        return orderJpaRepository
            .findAllByStatusInOrderByStatusDescNumberAsc(
                setOf(
                    OrderStatus.CONFIRMED,
                    OrderStatus.PREPARING,
                    OrderStatus.COMPLETED,
                ),
            )
            .map(mapper::toDomain)
    }

    override fun findByOrderNumber(number: Long): Order? {
        return orderJpaRepository.findById(number)
            .map(mapper::toDomain)
            .orElse(null)
    }

    override fun findByStatus(status: OrderStatus): List<Order> {
        return orderJpaRepository.findByStatus(status)
            .map(mapper::toDomain)
    }

    override fun findByCustomerId(customerId: UUID): List<Order> {
        return orderJpaRepository.findByCustomerId(customerId)
            .map(mapper::toDomain)
    }

    override fun findByCustomerIdAndStatus(
        customerId: UUID,
        status: OrderStatus,
    ): List<Order> {
        return orderJpaRepository.findByCustomerIdAndStatus(customerId, status)
            .map(mapper::toDomain)
    }

    override fun upsert(order: Order): Order {
        val currentOrder = order.number?.let { findByOrderNumber(number = order.number) } 
            ?: order.copy(number = null)
        val orderUpdated =
            currentOrder
                .copy(
                    orderedAt = order.orderedAt,
                    status = order.status,
                    customer = order.customer,
                    items = order.items,
                    total = order.total,
                )
        return persist(orderUpdated)
    }

    override fun deleteAll() {
        orderJpaRepository.deleteAll()
    }
    
    private fun persist(order: Order): Order =
        order
            .let(mapper::toEntity)
            .let(orderJpaRepository::save)
            .let(mapper::toDomain)
}
