package br.com.microservices.orchestrated.inventoryservicekotlin.core.consumer;

import br.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException
import br.com.microservices.orchestrated.inventoryservicekotlin.core.service.InventoryService
import br.com.microservices.orchestrated.inventoryservicekotlin.core.utils.JsonUtil
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class InventoryConsumer(

    val jsonUtil: JsonUtil,
    val inventoryService: InventoryService
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    @KafkaListener(
        groupId = "\${spring.kafka.consumer.group-id}",
        topics = ["\${spring.kafka.topic.inventory-success}"],
    )
    fun consumeInventorySuccessEvent(payload: String) {
        log.info("Receiving event {} from inventory-success topic", payload);
        val event = jsonUtil.toEvent(payload) ?: throw ValidationException("Cannot parser payload ${payload}");
        inventoryService.updateInventory(event);
    }

    @KafkaListener(
        groupId = "\${spring.kafka.consumer.group-id}",
        topics = ["\${spring.kafka.topic.inventory-fail}"]
    )
    fun consumeInventoryFailEvent(payload: String) {
        log.info("Receiving rollback event {} from inventory-fail topic", payload);
        val event = jsonUtil.toEvent(payload)?: throw ValidationException("Cannot parser payload ${payload}");
        inventoryService.rollbackInventory(event)
    }
}
