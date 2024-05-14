package com.fiap.order.driver.database.persistence.entities

import jakarta.persistence.*

@Entity
@Table(name = "component")
class ComponentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "component_number")
    val number: Long?,
    @Column(name = "component_name")
    val name: String,
)
