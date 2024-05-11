package br.com.microservices.orchestrated.inventoryservicekotlin.core.dto;

import br.com.microservices.orchestrated.inventoryservicekotlin.core.enums.ESagaStatus

import java.time.LocalDateTime

data class Event(
    var id: String,
    var transactionId: String,
    var orderId: String,
    var payload: Order,
    var source: String,
    var status: ESagaStatus,
    var eventHistory: MutableList<History>?,
    var createdAt: LocalDateTime
) {
    fun addToHistory(history: History) {
        eventHistory?.let {
            it.add(history)
        } ?: let {
            eventHistory = mutableListOf(history)
        }
    }
}
