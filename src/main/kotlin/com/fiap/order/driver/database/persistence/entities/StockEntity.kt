package com.fiap.order.driver.database.persistence.entities

import jakarta.persistence.*

@Entity
@Table(name = "stock")
class StockEntity(
    @Id
    @Column(name = "stock_component_number")
    val componentNumber: Long,
    @Column(name = "stock_quantity")
    val quantity: Long,
    @OneToOne
    @JoinColumn(
        name = "stock_component_number",
        referencedColumnName = "component_number",
        insertable = false,
        updatable = false,
    )
    val component: ComponentEntity?,
)
