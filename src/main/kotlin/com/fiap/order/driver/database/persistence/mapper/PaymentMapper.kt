package com.fiap.order.driver.database.persistence.mapper

import com.fiap.order.domain.entities.Payment
import com.fiap.order.driver.database.persistence.entities.PaymentEntity
import org.mapstruct.Mapper

@Mapper
interface PaymentMapper {
    fun toDomain(entity: PaymentEntity): Payment

    fun toEntity(domain: Payment): PaymentEntity
}
