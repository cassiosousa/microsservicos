package br.com.microservices.orchestrated.orchestratorservice.core.producer;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.ETopics;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorProducer {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendEvent(String payload, ETopics topic) {
        try {
            log.info("Sending event to topic {} with data {}", topic.getTopic(), payload);
            kafkaTemplate.send(topic.getTopic(), payload);
        }catch (Exception e) {
            log.error("Error trying to send data to topic {} with data {}", topic.getTopic(), payload, e);
        }
    }
}
