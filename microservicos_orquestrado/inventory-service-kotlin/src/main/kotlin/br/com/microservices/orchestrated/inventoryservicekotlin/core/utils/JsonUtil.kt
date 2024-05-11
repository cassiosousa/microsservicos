package br.com.microservices.orchestrated.inventoryservicekotlin.core.utils;

import br.com.microservices.orchestrated.inventoryservicekotlin.core.dto.Event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component


@Component
class JsonUtil(
    val objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    fun toJson(any: Event): String {
        return try {
            objectMapper.writeValueAsString(any);
        } catch (e: Exception) {
            "";
        }
    }

    fun toEvent(json: String): Event? {
        return try {
            objectMapper.readValue(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null;
        }
    }
}
