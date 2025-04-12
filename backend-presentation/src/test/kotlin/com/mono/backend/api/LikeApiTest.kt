package com.mono.backend.api

import com.mono.backend.like.response.ArticleLikeResponse
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LikeApiTest {
    private val restClient = RestClient.create("http://localhost:3001")

    @Test
    fun likeAndUnlikeTest() {
        val articleId = 9999L

        like(articleId, 1L, "pessimistic-lock-1")
        like(articleId, 2L, "pessimistic-lock-1")
        like(articleId, 3L, "pessimistic-lock-1")
        val response1 = read(articleId, 1L)
        val response2 = read(articleId, 2L)
        val response3 = read(articleId, 3L)

        println(response1)
        println(response2)
        println(response3)

        unLike(articleId, 1L)
        unLike(articleId, 2L)
        unLike(articleId, 3L)


    }

    private fun like(articleId: Long, userId: Long, lockType: String) {
        restClient.post()
            .uri("/v1/article-like/articles/$articleId/user/$userId/$lockType")
            .retrieve()
    }

    private fun unLike(articleId: Long, userId: Long) {
        restClient.delete()
            .uri("/v1/article-like/articles/$articleId/users/$userId")
            .retrieve()
    }

    private fun read(articleId: Long, userId: Long): ArticleLikeResponse? {
        return restClient.get()
            .uri("/v1/article-like/articles/$articleId/user/$userId")
            .retrieve()
            .body(ArticleLikeResponse::class.java)
    }

    @Test
    fun likePerformanceTest() {
        val executorService = Executors.newFixedThreadPool(100)
        likePerformanceTest(executorService, 4444L, "pessimistic-lock-1")
        likePerformanceTest(executorService, 5555L, "pessimistic-lock-2")
        likePerformanceTest(executorService, 6666L, "optimistic-lock")

    }

    private fun likePerformanceTest(executorService: ExecutorService, articleId: Long, lockType: String) {
        val latch = CountDownLatch(3000)
        println("$lockType Start")

        like(articleId, 1L, lockType)

        val start = System.nanoTime()
        for (i in 1L..3000) {
            val userId: Long = i + 2
            executorService.submit {
                like(articleId, userId, lockType)
                latch.countDown()
            }
        }

        latch.await()
        val end = System.nanoTime()

        println("lockType = $lockType, time = ${(end - start) / 1000000L}ms")
        println("$lockType end")

        val count = restClient.get()
            .uri("/v1/article-like/articles/$articleId/count")
            .retrieve()
            .body(Long::class.java)

        println("count = $count")
    }
}