package com.mono.backend.webclient.exrate

import com.fasterxml.jackson.databind.ObjectMapper
import com.mono.backend.webclient.common.WebClientPair
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import reactor.util.retry.Retry
import java.math.BigDecimal

@Service
@DependsOn("webClientFactory")
class ErApiClient(
    @Qualifier("erApiWebClientPair") webClientPair: WebClientPair,
) {
    private val webClient = webClientPair.webClient
    private val retrySpec = Retry.backoff(
        webClientPair.properties.maxRetry, webClientPair.properties.retryDelay
    )

    suspend fun getExRate(currency: String): BigDecimal = webClient
        .get()
        .uri(currency)
        .retrieve()
        .bodyToMono(String::class.java)
        .retryWhen(retrySpec)
        .map {
            val mapper = ObjectMapper()
            val data = mapper.readValue(it, ExRateData::class.java)
            data.rates["KRW"] ?: BigDecimal.ZERO
        }
        .awaitSingle()
}