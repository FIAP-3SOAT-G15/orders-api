package com.fiap.order.driver.database.persistence.entities

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "product")
class ProductEntity(
    @Id
    @Column(name = "product_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val number: Long?,
    @Column(name = "product_order_number")
    val orderNumber: Long?,
    @Column(name = "product_name")
    val name: String,
    @Column(name = "product_category")
    val category: String,
    @Column(name = "product_price")
    val price: BigDecimal,
    @Column(name = "product_description")
    val description: String? = null,
)
