package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.OrderGateway
import com.fiap.order.domain.entities.Order
import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.database.persistence.jpa.OrderJpaRepository
import com.fiap.order.driver.database.persistence.mapper.OrderMapper
import org.mapstruct.factory.Mappers
import java.util.*

class OrderGatewayImpl(
    private val orderRepository: OrderJpaRepository,
) : OrderGateway {
    private val mapper = Mappers.getMapper(OrderMapper::class.java)

    override fun findAll(): List<Order> = 
        orderRepository.findAll().map(mapper::toDomain)
    
    override fun findAllActive(): List<Order>
        = orderRepository
            .findAllByStatusInOrderByStatusDescNumberAsc(
                setOf(
                    OrderStatus.CONFIRMED,
                    OrderStatus.PREPARING,
                    OrderStatus.COMPLETED,
                ),
            )
            .map(mapper::toDomain)

    override fun findByOrderNumber(number: Long): Order? =
        orderRepository.findById(number).map(mapper::toDomain).orElse(null)

    override fun findByStatus(status: OrderStatus): List<Order> =
        orderRepository.findByStatus(status).map(mapper::toDomain)

    override fun findByStatusAndCustomerId(status: OrderStatus, customerId: UUID): List<Order> =
        orderRepository.findByStatusAndCustomerId(status, customerId.toString()).map(mapper::toDomain)
    
    override fun findByCustomerId(customerId: UUID): List<Order> =
        orderRepository.findByCustomerId(customerId.toString()).map(mapper::toDomain)

    override fun upsert(order: Order): Order {
        val currentOrder = order.number?.let { findByOrderNumber(number = order.number) } 
            ?: order.copy(number = null)
        val orderUpdated =
            currentOrder
                .copy(
                    orderedAt = order.orderedAt,
                    customer = order.customer,
                    status = order.status,
                    lines = order.lines,
                    total = order.total,
                )
        return persist(orderUpdated)
    }

    override fun deleteAll() {
        orderRepository.deleteAll()
    }
    
    private fun persist(order: Order): Order =
        order
            .let(mapper::toEntity)
            .let(orderRepository::save)
            .let(mapper::toDomain)
}
