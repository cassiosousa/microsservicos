package br.com.microservices.orchestrated.inventoryservicekotlin.core.service;

import br.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException
import br.com.microservices.orchestrated.inventoryservicekotlin.core.dto.Event
import br.com.microservices.orchestrated.inventoryservicekotlin.core.dto.History
import br.com.microservices.orchestrated.inventoryservicekotlin.core.dto.Order
import br.com.microservices.orchestrated.inventoryservicekotlin.core.dto.OrderProducts
import br.com.microservices.orchestrated.inventoryservicekotlin.core.enums.ESagaStatus
import br.com.microservices.orchestrated.inventoryservicekotlin.core.model.Inventory
import br.com.microservices.orchestrated.inventoryservicekotlin.core.model.OrderInventory
import br.com.microservices.orchestrated.inventoryservicekotlin.core.producer.KafkaProducer
import br.com.microservices.orchestrated.inventoryservicekotlin.core.repository.InventoryRepository
import br.com.microservices.orchestrated.inventoryservicekotlin.core.repository.OrderInventoryRepository
import br.com.microservices.orchestrated.inventoryservicekotlin.core.utils.JsonUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class InventoryService(
    val jsonUtil: JsonUtil,
    val producer: KafkaProducer,

    val inventoryRepository: InventoryRepository,
    val orderInventoryRepository: OrderInventoryRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val CURRENT_SOURCE = "INVENTORY_SERVICE"
    }

    fun updateInventory(event: Event) {
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
            updateInventory(event.payload);
            handleSuccess(event);
        } catch (e: Exception) {
            log.error("Error trying to update inventory: ", e);
            handleFailCurrentNotExecuted(event, e.message ?: "");
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    fun updateInventory(order: Order) = order.products
        .forEach {
            val inventory = findByInventoryProductCode(it.product.code)
            checkInventory(inventory.available, it.quantity)
            inventory.available -= it.quantity
            inventoryRepository.save(inventory)
        }


    private fun checkInventory(available: Int, orderQuantity: Int) {
        if (orderQuantity > available) {
            throw ValidationException(("Product is out of stock!"));
        }
    }

    fun checkCurrentValidation(event: Event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.orderId, event.transactionId)) {
            throw ValidationException("Inventory not found by informed product.");
        }
    }

    private fun createOrderInventory(event: Event) = event.payload.products
        .forEach {
            var inventory = findByInventoryProductCode(it.product.code);
            var orderInventory = createOrderInventory(event, it, inventory);
            orderInventoryRepository.save(orderInventory);
        }


    private fun createOrderInventory(event: Event, product: OrderProducts, inventory: Inventory): OrderInventory =
        OrderInventory(
            inventory = inventory,
            oldQuantity = inventory.available,
            orderQuantity = product.quantity,
            newQuantity = inventory.available - product.quantity,
            orderId = event.payload.id,
            transactionId = event.transactionId
        )


    private fun findByInventoryProductCode(productCode: String): Inventory =
        inventoryRepository.findByProductCode(productCode)?.let { inventory -> inventory }
            ?: throw ValidationException("Inventory not found by informed product.")


    private fun handleSuccess(event: Event) {
        event.status = ESagaStatus.SUCCESS
        event.source = CURRENT_SOURCE
        addHistory(event, "Inventory updated successfully");
    }

    private fun addHistory(event: Event, message: String) {
        var history = History(
            source = event.source,
            status = event.status,
            message = message,
            createdAt = LocalDateTime.now()
        )
        event.addToHistory(history);
    }

    private fun handleFailCurrentNotExecuted(event: Event, message: String) {
        event.status = ESagaStatus.ROLLBACK_PENDING
        event.source = CURRENT_SOURCE
        addHistory(event, "Fail to update inventory: ${message}");
    }

    fun rollbackInventory(event: Event) {
        event.status = ESagaStatus.FAIL
        event.source = CURRENT_SOURCE
        try {
            returnInventoryToPreviousValues(event);
            addHistory(event, "Rollback executed for inventory!");
        } catch (e: Exception) {
            log.error("Rollback not executed for inventory!", e);
            addHistory(event, "Rollback not executed for inventory! ${e.message}");
        }
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private fun returnInventoryToPreviousValues(event: Event) =
        orderInventoryRepository.findByOrderIdAndTransactionId(event.payload.id, event.transactionId)
            .forEach {
                val inventory = it.inventory
                inventory.available = it.orderQuantity
                inventoryRepository.save(inventory);
                log.info(
                    "Restored inventory for order {} from {} to {}",
                    event.payload.id,
                    it.newQuantity,
                    inventory.available
                );
            }
}
