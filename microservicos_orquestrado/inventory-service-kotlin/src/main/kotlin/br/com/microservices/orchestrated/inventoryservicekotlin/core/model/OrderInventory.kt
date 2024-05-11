package br.com.microservices.orchestrated.inventoryservicekotlin.core.model;

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_inventory")
data class OrderInventory(


    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    val inventory: Inventory,

    @Column(nullable = false)
    val orderId: String,

    @Column(nullable = false)
    val transactionId: String,

    @Column(nullable = false)
    val orderQuantity: Int,

    @Column(nullable = false)
    val oldQuantity: Int,

    @Column(nullable = false)
    val newQuantity: Int,

    ) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null

    @PrePersist
    fun prePersist() {
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
