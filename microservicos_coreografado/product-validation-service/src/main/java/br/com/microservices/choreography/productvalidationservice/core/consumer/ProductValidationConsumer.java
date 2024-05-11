package br.com.microservices.choreography.productvalidationservice.core.consumer;

import br.com.microservices.choreography.productvalidationservice.core.service.ProductValidationService;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {

    private final JsonUtil jsonUtil;
    private final ProductValidationService productValidationService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-start}"
    )
    public void consumeProductValidationStartEvent(String payload) {
        log.info("Receiving event {} from product-validation-start topic", payload);
        var event = jsonUtil.toEvent(payload);
        productValidationService.validateExistingProducts(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-fail}"
    )
    public void consumeProductValidationFailEvent(String payload) {
        log.info("Receiving event {} from product-validation-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        productValidationService.rollbackEvent(event);
    }
}