package br.com.microservices.orchestrated.inventoryservicekotlin.core.service

import br.com.microservices.orchestrated.inventoryservicekotlin.AbstractPostgresTest
import br.com.microservices.orchestrated.inventoryservicekotlin.core.dto.Event
import br.com.microservices.orchestrated.inventoryservicekotlin.core.enums.ESagaStatus
import br.com.microservices.orchestrated.inventoryservicekotlin.core.model.OrderInventory
import br.com.microservices.orchestrated.inventoryservicekotlin.core.producer.KafkaProducer
import br.com.microservices.orchestrated.inventoryservicekotlin.core.repository.InventoryRepository
import br.com.microservices.orchestrated.inventoryservicekotlin.core.repository.OrderInventoryRepository
import br.com.microservices.orchestrated.inventoryservicekotlin.core.utils.JsonUtil
import br.com.microservices.orchestrated.inventoryservicekotlin.kafka.inventoryFailPayload
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfiguration
@ExtendWith(MockKExtension::class)
internal class InventoryServiceTest : AbstractPostgresTest() {

    @MockK
    lateinit var kafkaProducer: KafkaProducer

    @MockK
    lateinit var inventoryRepositoryMock: InventoryRepository

    @SpyK
    @InjectMockKs(overrideValues = true)
    lateinit var inventoryService: InventoryService

    @Autowired
    lateinit var orderInventoryRepository: OrderInventoryRepository

    @Autowired
    lateinit var inventoryRepositoryLocal: InventoryRepository

    @Autowired
    lateinit var jsonUtil: JsonUtil
    lateinit var payloadFail: Event

    @BeforeEach
    fun setUp() {
        payloadFail = jsonUtil.toEvent(inventoryFailPayload) ?: fail("inventoryFailPayload error")
        orderInventoryRepository.deleteAll()

        val inventory = inventoryRepositoryLocal.findByProductCode("COMIC_BOOKS") ?: fail()
        val quantity = payloadFail.payload.products.first().quantity

        orderInventoryRepository.save(
            OrderInventory(
                inventory = inventory,
                oldQuantity = inventory.available,
                orderQuantity = quantity,
                newQuantity = inventory.available - quantity,
                orderId = payloadFail.payload.id,
                transactionId = payloadFail.transactionId
            )
        )
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun updateInventory() {
    }

    @Test
    fun `should rollbackInventory and send message to kafkaProducer orchestrator`() {
        val capturePayload = slot<String>()
        every {
            kafkaProducer.sendEvent(capture(capturePayload))
        } answers {
            println("InventoryPayload: ${capturePayload.captured}")
        }

        inventoryService.rollbackInventory(payloadFail)

        val capturedPayload = jsonUtil.toEvent(capturePayload.captured) ?: fail()
        val eventHistory = capturedPayload.eventHistory?.first() ?: fail("Event not founded")
        assertThat(capturedPayload.source).isEqualTo("INVENTORY_SERVICE")
        assertThat(capturedPayload.status).isEqualTo(ESagaStatus.FAIL)
        assertThat(eventHistory.source).isEqualTo("INVENTORY_SERVICE")
        assertThat(eventHistory.status).isEqualTo(ESagaStatus.FAIL)
        assertThat(eventHistory.message).isEqualTo("Rollback executed for inventory!")

        verify(exactly = 1) { kafkaProducer.sendEvent(any()) }
    }

    @Test
    fun `should rollbackInventory and exception occurs then Rollback not executed`() {
        val inventoryService =
            InventoryService(jsonUtil, kafkaProducer, inventoryRepositoryMock, orderInventoryRepository)
        val messageException = "Message Exception!"
        val capturePayload = slot<String>()
        every {
            kafkaProducer.sendEvent(capture(capturePayload))
        } answers {
            println("InventoryPayload: ${capturePayload.captured}")
        }
        every { inventoryRepositoryMock.save(any()) } throws Exception(messageException)

        inventoryService.rollbackInventory(payloadFail)

        val capturedPayload = jsonUtil.toEvent(capturePayload.captured) ?: fail()
        val eventHistory = capturedPayload.eventHistory?.first() ?: fail("Event not founded")
        assertThat(capturedPayload.source).isEqualTo("INVENTORY_SERVICE")
        assertThat(capturedPayload.status).isEqualTo(ESagaStatus.FAIL)
        assertThat(eventHistory.source).isEqualTo("INVENTORY_SERVICE")
        assertThat(eventHistory.status).isEqualTo(ESagaStatus.FAIL)
        assertThat(eventHistory.message).contains("Rollback not executed for inventory! $messageException")

        verify(exactly = 1) { kafkaProducer.sendEvent(any()) }
    }
}