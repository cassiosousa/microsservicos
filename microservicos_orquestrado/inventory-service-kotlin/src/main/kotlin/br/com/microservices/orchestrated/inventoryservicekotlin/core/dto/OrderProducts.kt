package br.com.microservices.orchestrated.inventoryservicekotlin.core.dto;


data class OrderProducts(
    val product: Product,
    val quantity: Int
)
