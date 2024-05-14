package com.fiap.order.driver.database.persistence.jpa

import com.fiap.order.driver.database.persistence.entities.ProductEntity
import org.springframework.data.repository.CrudRepository

interface ProductJpaRepository : CrudRepository<ProductEntity, Long>
