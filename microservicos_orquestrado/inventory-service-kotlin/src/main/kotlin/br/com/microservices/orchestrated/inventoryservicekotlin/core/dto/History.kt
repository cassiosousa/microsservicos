package br.com.microservices.orchestrated.inventoryservicekotlin.core.dto;

import br.com.microservices.orchestrated.inventoryservicekotlin.core.enums.ESagaStatus
import java.time.LocalDateTime

data class History(
    val source: String,
    val status: ESagaStatus,
    val message: String,
    val createdAt: LocalDateTime
)
