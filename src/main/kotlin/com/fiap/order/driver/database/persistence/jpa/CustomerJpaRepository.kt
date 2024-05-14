package com.fiap.order.driver.database.persistence.jpa

import com.fiap.order.driver.database.persistence.entities.CustomerEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CustomerJpaRepository : CrudRepository<CustomerEntity, String> {
    fun findByEmail(email: String): Optional<CustomerEntity>

    fun findByDocument(document: String): Optional<CustomerEntity>
}
