package com.mono.backend.api

import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class ViewApiTest {
    private val restClient = RestClient.create("http://localhost:3001")

    @Test
    fun viewTest() {
        val executorService = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)


        for (i in 0 until 10000) {
            executorService.submit {
                restClient.post()
                    .uri("/v1/article-views/articles/{articleId}/users/{userId}", 6L, 2L)
                    .retrieve()
                latch.countDown()
            }
        }

        latch.await()

        val count = restClient.get()
            .uri("/v1/article-views/articles/{articleId}/count", 6L)
            .retrieve()
            .body(Long::class.java)

        println("count = $count")
    }
}