package com.fiap.order.driver.database.persistence.jpa

import com.fiap.order.driver.database.persistence.entities.StockEntity
import org.springframework.data.repository.CrudRepository

interface StockJpaRepository : CrudRepository<StockEntity, Long>
