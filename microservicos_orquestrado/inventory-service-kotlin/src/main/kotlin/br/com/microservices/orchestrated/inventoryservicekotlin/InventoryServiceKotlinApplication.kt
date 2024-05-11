package br.com.microservices.orchestrated.inventoryservicekotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InventoryServiceKotlinApplication

fun main(args: Array<String>) {
	runApplication<InventoryServiceKotlinApplication>(*args)
}
