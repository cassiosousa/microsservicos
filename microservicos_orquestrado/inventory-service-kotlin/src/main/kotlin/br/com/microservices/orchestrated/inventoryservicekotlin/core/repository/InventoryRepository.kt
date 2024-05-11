package br.com.microservices.orchestrated.inventoryservicekotlin.core.repository;

import br.com.microservices.orchestrated.inventoryservicekotlin.core.model.Inventory
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository : JpaRepository<Inventory, Int> {

    fun findByProductCode(productCode: String): Inventory?;
}
