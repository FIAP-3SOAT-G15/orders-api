package com.fiap.order.driver.database.persistence.entities

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_line")
class OrderLineEntity(
    @Id
    @Column(name = "order_line_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val number: Long,

    @Column(name = "order_line_order_number")
    val orderNumber: Long,

    @Column(name = "order_line_product_number")
    val productNumber: Long,

    @Column(name = "order_line_name")
    val name: String,

    @Column(name = "order_line_description")
    val description: String,

    @Column(name = "order_line_unit_price")
    val unitPrice: BigDecimal,

    @Column(name = "order_line_quantity")
    val quantity: Long,

    @Column(name = "order_line_total")
    val total: BigDecimal,
)
