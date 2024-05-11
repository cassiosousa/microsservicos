package br.com.microservices.orchestrated.inventoryservicekotlin

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ValueBean(
    @Value("\${spring.kafka.consumer.group-id}")
    val groupId: String,

    @Value("\${spring.kafka.topic.inventory-success}")
    val inventorySuccessTopic: String = "inventory-success"
)