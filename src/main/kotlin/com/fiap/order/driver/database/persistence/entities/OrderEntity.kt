package com.fiap.order.driver.database.persistence.entities

import com.fiap.order.domain.valueobjects.OrderStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "order")
class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_number")
    val number: Long,

    @Column(name = "order_ordered_at")
    val orderedAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_customer_id")
    val customer: CustomerEntity? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    val status: OrderStatus,

    @OneToMany(mappedBy="orderNumber", cascade = [CascadeType.MERGE])
    val lines: List<OrderLineEntity>,

    @Column(name = "order_total")
    val total: BigDecimal,
)
