package com.fiap.order.driver.database.persistence.jpa

import com.fiap.order.driver.database.persistence.entities.PaymentEntity
import org.springframework.data.repository.CrudRepository

interface PaymentJpaRepository : CrudRepository<PaymentEntity, Long>
