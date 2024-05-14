package com.fiap.order.adapter.gateway.impl

import com.fiap.order.adapter.gateway.PaymentGateway
import com.fiap.order.domain.entities.Payment
import com.fiap.order.domain.errors.ErrorType
import com.fiap.order.domain.errors.SelfOrderManagementException
import com.fiap.order.driver.database.persistence.jpa.PaymentJpaRepository
import com.fiap.order.driver.database.persistence.mapper.PaymentMapper
import org.mapstruct.factory.Mappers

class PaymentGatewayImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
) : PaymentGateway {
    private val mapper = Mappers.getMapper(PaymentMapper::class.java)

    override fun findByOrderNumber(orderNumber: Long): Payment {
        return paymentJpaRepository.findById(orderNumber)
            .map(mapper::toDomain)
            .orElse(null)
    }

    override fun create(payment: Payment): Payment {
        payment.orderNumber.let {
            findByOrderNumber(it).let {
                throw SelfOrderManagementException(
                    errorType = ErrorType.PAYMENT_ALREADY_EXISTS,
                    message = "Payment record for order [${payment.orderNumber}] already exists",
                )
            }
        }
        return persist(payment)
    }


    private fun persist(payment: Payment): Payment =
        payment
            .let(mapper::toEntity)
            .let(paymentJpaRepository::save)
            .let(mapper::toDomain)
}
