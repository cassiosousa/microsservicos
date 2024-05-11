package br.com.microservices.orchestrated.inventoryservicekotlin.kafka

import br.com.microservices.orchestrated.inventoryservicekotlin.AbstractIntegrationTest
import br.com.microservices.orchestrated.inventoryservicekotlin.ValueBean
import br.com.microservices.orchestrated.inventoryservicekotlin.core.enums.ESagaStatus
import br.com.microservices.orchestrated.inventoryservicekotlin.core.producer.KafkaProducer
import br.com.microservices.orchestrated.inventoryservicekotlin.core.repository.OrderInventoryRepository
import br.com.microservices.orchestrated.inventoryservicekotlin.core.utils.JsonUtil
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import java.util.concurrent.TimeUnit


class KafkaTest(
    @Autowired
    val kafkaTemplate: KafkaTemplate<String, String>,
    @Autowired
    val kafkaOrquestratorConsumer: KafkaConsumerOrchestrator,
    @Autowired
    val jsonUtil: JsonUtil,
    @Autowired
    val repository: OrderInventoryRepository,

    @Autowired
    val kafkaProducer: KafkaProducer,
    @Autowired
    val valueBean: ValueBean

) : AbstractIntegrationTest() {

    @Test
    @Order(1)
    fun `should produces event on topic inventory success`() {
        val transactionId = "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
        val orderId = "64429e987a8b646915b3735f"

        Thread.sleep(5 * 1000)

        kafkaTemplate.send(valueBean.inventorySuccessTopic, inventorySuccessPayload)


        kafkaOrquestratorConsumer.latch.await(10, TimeUnit.SECONDS)

        val existsInventory = repository.existsByOrderIdAndTransactionId(orderId, transactionId)
        val event = jsonUtil.toEvent(kafkaOrquestratorConsumer.payload) ?: fail("Event not founded.")
        assertThat(event.orderId).isEqualTo(orderId)
        assertThat(event.transactionId).isEqualTo(transactionId)
        assertThat(event.source).isEqualTo("INVENTORY_SERVICE")
        assertThat(event.status).isEqualTo(ESagaStatus.SUCCESS)
        assertThat(event.payload?.totalAmount).isEqualTo(56.4)
        assertThat(event.payload?.totalItems).isEqualTo(4)
        assertThat(existsInventory).isTrue()

        kafkaOrquestratorConsumer.resetLatch()

    }

    @Test
    @Order(2)
    fun `should produces event on topic inventory fail`() {
        val transactionId = "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
        val orderId = "64429e987a8b646915b3735f"

        kafkaTemplate.send(valueBean.inventorySuccessTopic, inventoryFailPayload)


        kafkaOrquestratorConsumer.latch.await(10, TimeUnit.SECONDS)

        val existsInventory = repository.existsByOrderIdAndTransactionId(orderId, transactionId)
        val event = jsonUtil.toEvent(kafkaOrquestratorConsumer.payload) ?: fail("Event not founded.")
        assertThat(event.orderId).isEqualTo(orderId)
        assertThat(event.transactionId).isEqualTo(transactionId)
        assertThat(event.source).isEqualTo("INVENTORY_SERVICE")
        assertThat(event.status).isEqualTo(ESagaStatus.ROLLBACK_PENDING)
        assertThat(event.payload?.totalAmount).isEqualTo(56.4)
        assertThat(event.payload?.totalItems).isEqualTo(4)
        assertThat(existsInventory).isTrue()

        kafkaOrquestratorConsumer.resetLatch()

    }

    @Test
    @Order(3)
    fun `should receive event on topic orchestrator`() {

        kafkaProducer.sendEvent(orchestratorPayload)

        kafkaOrquestratorConsumer.latch.await(20, TimeUnit.SECONDS)
        assertThat(orchestratorPayload).isEqualTo(kafkaOrquestratorConsumer.payload)
        kafkaOrquestratorConsumer.resetLatch()
    }
}

