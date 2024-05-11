package br.com.microservices.orchestrated.inventoryservicekotlin.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch


@Component
class KafkaConsumerOrchestrator {
    private val log = LoggerFactory.getLogger(KafkaConsumer::class.java)
    final var latch: CountDownLatch = CountDownLatch(1)
        private set
    final var payload: String = ""
        private set

    @KafkaListener(
        groupId = "\${spring.kafka.consumer.group-id}",
        topics = ["\${spring.kafka.topic.orchestrator}"]
    )
    fun receive(consumerRecord: ConsumerRecord<*, String>) {
        log.info("received payload='{}'", consumerRecord.toString())
        payload = consumerRecord.value()
        latch.countDown()
    }

    fun resetLatch() {
        latch = CountDownLatch(1)
        payload = ""
    } // other getters

}