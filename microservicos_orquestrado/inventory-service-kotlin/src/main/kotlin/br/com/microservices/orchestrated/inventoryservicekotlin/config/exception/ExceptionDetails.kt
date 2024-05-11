package br.com.microservices.orchestrated.inventoryservice.config.exception;


data class ExceptionDetails(
        val status: Int,
        val message: String?
) {
}
