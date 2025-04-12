package com.mono.backend.dataserializer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DataSerializer {
    private val objectMapper = initialize()
    private val logger: Logger = LoggerFactory.getLogger(DataSerializer::class.java)

    private fun initialize(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun <T> deserialize(data: String?, clazz: Class<T>): T? {
        return try {
            objectMapper.readValue(data, clazz)
        } catch (e: JsonProcessingException) {
            logger.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz.simpleName, e)
            null
        }
    }

    fun <T> deserialize(data: Any?, clazz: Class<out T>?): T? {
        return objectMapper.convertValue(data, clazz)
    }

    fun serialize(any: Any): String? {
        return try {
            objectMapper.writeValueAsString(any)
        } catch (e: JsonProcessingException) {
            logger.error("[DataSerializer.serialize] object={}", any, e)
            null
        }
    }
}