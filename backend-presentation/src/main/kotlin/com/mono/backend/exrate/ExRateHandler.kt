package com.mono.backend.exrate

import com.mono.backend.common.DefaultHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.jvm.optionals.getOrNull

@Component
class ExRateHandler(
    private val exRateService: ExRateService
) : DefaultHandler {
    suspend fun getExRate(serverRequest: ServerRequest): ServerResponse {
        serverRequest.queryParam("currency").getOrNull().let {
            return ok(exRateService.getExRate(it!!))
        }
    }
}