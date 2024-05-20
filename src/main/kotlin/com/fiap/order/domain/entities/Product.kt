package com.fiap.order.domain.entities

import java.math.BigDecimal

data class Product (
    val number: Long,
    val name: String,
    val description: String,
    val price: BigDecimal
)
