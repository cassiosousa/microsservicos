package br.com.microservices.orchestrated.inventoryservicekotlin.core.model;

import jakarta.persistence.*

@Entity
@Table(name = "inventory")
data class Inventory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    val productCode: String,

    @Column(nullable = false)
    var available: Int
)
