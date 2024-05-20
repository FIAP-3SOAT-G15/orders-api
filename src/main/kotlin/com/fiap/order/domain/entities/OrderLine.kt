package com.fiap.order.domain.entities

import java.math.BigDecimal

/**
 * Why storing product information in order service?
 * For the history. The products will change in the stock service
 * (in terms of ID (product number), unit price, name, description, etc),
 * so storing the stock external IDs here could not be enough
 * to understand the order in the future.
 */
data class OrderLine(
    val number: Long? = null,
    val orderNumber: Long? = null,
    val productNumber: Long,

    // DO NOT REMOVE
    val name: String,
    val description: String,
    val unitPrice: BigDecimal,
    
    val quantity: Long,
    val total: BigDecimal,
)
