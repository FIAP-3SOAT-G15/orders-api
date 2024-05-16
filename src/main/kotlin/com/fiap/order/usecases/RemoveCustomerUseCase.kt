package com.fiap.order.usecases

import com.fiap.order.domain.entities.Customer
import java.util.*

interface RemoveCustomerUseCase {
    fun remove(customerId: UUID): Customer
}
