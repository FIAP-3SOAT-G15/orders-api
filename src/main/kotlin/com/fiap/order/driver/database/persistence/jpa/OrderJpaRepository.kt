package com.fiap.order.driver.database.persistence.jpa

import com.fiap.order.domain.valueobjects.OrderStatus
import com.fiap.order.driver.database.persistence.entities.OrderEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface OrderJpaRepository : CrudRepository<OrderEntity, Long> {
    fun findByStatus(status: OrderStatus): List<OrderEntity>

    fun findByCustomerId(customerId: String): List<OrderEntity>

    fun findByStatusAndCustomerId(status: OrderStatus, customerId: String): List<OrderEntity>

    fun findAllByStatusInOrderByStatusDescNumberAsc(status: Set<OrderStatus>): List<OrderEntity>
}
