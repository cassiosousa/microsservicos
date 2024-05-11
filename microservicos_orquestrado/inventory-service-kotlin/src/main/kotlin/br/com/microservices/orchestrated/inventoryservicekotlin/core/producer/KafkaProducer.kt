package br.com.microservices.orchestrated.inventoryservicekotlin.core.producer;

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    @Value("\${spring.kafka.topic.orchestrator}")
    val orchestratorTopic: String,
    val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    fun sendEvent(payload: String) {
        try {
            log.info("Sending event to topic {} with data {}", orchestratorTopic, payload);
            kafkaTemplate.send(orchestratorTopic, payload);
        } catch (e: Exception) {
            log.error("Error trying to send data to topic {} with data {}", orchestratorTopic, payload, e);
        }
    }
}
