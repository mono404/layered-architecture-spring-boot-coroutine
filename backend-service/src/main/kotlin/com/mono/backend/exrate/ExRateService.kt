package com.mono.backend.exrate

import com.mono.backend.webclient.exrate.ErApiClient
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ExRateService(
    private val erApiClient: ErApiClient,
) {
    suspend fun getExRate(currency: String): BigDecimal {
        return erApiClient.getExRate(currency)
    }
}