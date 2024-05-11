package br.com.microservices.orchestrated.inventoryservicekotlin

import br.com.microservices.orchestrated.inventoryservicekotlin.kafka.KafkaTest
import org.springframework.boot.fromApplication

fun main(args: Array<String>) {
	fromApplication<InventoryServiceKotlinApplication>()
		.with(KafkaTest::class.java)
		.run(*args)
}
