package com.mono.backend.api

import com.mono.backend.article.request.ArticleCreateRequest
import com.mono.backend.article.request.ArticleUpdateRequest
import com.mono.backend.article.response.ArticlePageResponse
import com.mono.backend.article.response.ArticleResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class ArticleApiTest {
    private val restClient = RestClient.create("http://localhost:3001")

    @Test
    fun createTest() {
        val response = create(ArticleCreateRequest("hi", "my content", 1L, 1L))
        println(response)
    }

    @Test
    fun readTest() {
        val response = read(168288352171847680)
        println(response)
    }

    @Test
    fun updateTest() {
        update(168516975047135232)
        val response = read(168516975047135232)
        println(response)
    }

    @Test
    fun deleteTest() {
        restClient.delete()
            .uri("/v1/articles/168288352171847680")
            .retrieve()
    }

    @Test
    fun readAllTest() {
        val response = restClient.get()
            .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
            .retrieve()
            .body(ArticlePageResponse::class.java)

        println("response.articleCount : ${response?.articleCount}")
        for (article in response?.articles!!) {
            println("articleId = ${article.articleId}")
        }
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val article1 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})

        println("firstPage")
        for (article in article1!!) {
            println("articleId = ${article.articleId}")
        }

        val lastArticleId = article1.last().articleId
        val article2 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=$lastArticleId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})

        println("secondPage")
        for (article in article2!!) {
            println("articleId = ${article.articleId}")
        }

    }

    @Test
    fun countTest() {
        val response = create(ArticleCreateRequest("hi", "content", 1L, 2L))

        val count1 = restClient.get()
            .uri("/v1/articles/boards/2/count")
            .retrieve()
            .body(Long::class.java)

        println("count1 = $count1")

        restClient.delete()
            .uri("/v1/articles/${response?.articleId}")
            .retrieve()

        val count2 = restClient.get()
            .uri("/v1/articles/boards/2/count")
            .retrieve()
            .body(Long::class.java)

        println("count2 = $count2")
    }

    private fun create(request: ArticleCreateRequest): ArticleResponse? {
        return restClient.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    private fun read(articleId: Long): ArticleResponse? {
        return restClient.get()
            .uri("/v1/articles/$articleId")
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    private fun update(articleId: Long) {
        restClient.put()
            .uri("/v1/articles/$articleId")
            .body(ArticleUpdateRequest("hi 2", "my content 22"))
            .retrieve()
            .body(ArticleResponse::class.java)
    }
}