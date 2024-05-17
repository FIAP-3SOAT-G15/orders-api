package com.fiap.order.driver.database.persistence.entities

import com.fiap.order.domain.valueobjects.OrderStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "order")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_number")
    val number: Long,
    @Column(name = "order_date")
    val date: LocalDate,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_customer_id")
    val customer: CustomerEntity? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    val status: OrderStatus,
    @OneToMany(mappedBy="orderNumber", cascade = [CascadeType.MERGE])
    val items: List<ProductEntity>,
    @Column(name = "order_total")
    val total: BigDecimal,
)
