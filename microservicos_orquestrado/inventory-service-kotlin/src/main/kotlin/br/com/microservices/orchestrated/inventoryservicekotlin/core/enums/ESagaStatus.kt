package br.com.microservices.orchestrated.inventoryservicekotlin.core.enums;

enum class ESagaStatus {
    SUCCESS,
    ROLLBACK_PENDING,
    FAIL
}
