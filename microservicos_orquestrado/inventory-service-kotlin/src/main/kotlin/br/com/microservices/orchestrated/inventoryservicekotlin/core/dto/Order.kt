package br.com.microservices.orchestrated.inventoryservicekotlin.core.dto;

import java.time.LocalDateTime


data class Order(
    val id: String,
    val products: Collection<OrderProducts>,
    val createdAt: LocalDateTime,
    val transactionId: String,
    val totalAmount: Double,
    val totalItems: Int

)
