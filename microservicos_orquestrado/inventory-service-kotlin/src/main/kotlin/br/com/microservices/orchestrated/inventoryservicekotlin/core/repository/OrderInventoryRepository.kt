package br.com.microservices.orchestrated.inventoryservicekotlin.core.repository;

import br.com.microservices.orchestrated.inventoryservicekotlin.core.model.OrderInventory
import org.springframework.data.jpa.repository.JpaRepository

interface OrderInventoryRepository : JpaRepository<OrderInventory, Int> {

    fun existsByOrderIdAndTransactionId(orderId: String, transactionId: String): Boolean
    fun findByOrderIdAndTransactionId(orderId: String, transactionId: String): Collection<OrderInventory>
}
