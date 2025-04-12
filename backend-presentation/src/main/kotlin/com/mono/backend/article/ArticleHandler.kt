package com.mono.backend.article

import com.mono.backend.article.request.ArticleCreateRequest
import com.mono.backend.article.request.ArticleUpdateRequest
import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import kotlin.jvm.optionals.getOrNull

@Component
class ArticleHandler(
    private val articleService: ArticleService
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        return articleService.read(articleId)?.let { ok(it) } ?: noContent()
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(articleService.readAll(boardId, page, pageSize))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.queryParam("boardId").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val lastArticleId = serverRequest.queryParam("lastArticleId").getOrNull()?.toLong()
        return ok(articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val requestBody = serverRequest.awaitBody(ArticleCreateRequest::class)
        return ok(articleService.create(requestBody))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val requestBody = serverRequest.awaitBody(ArticleUpdateRequest::class)
        return ok(articleService.update(articleId, requestBody))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        return ok(articleService.delete(articleId))
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val boardId = serverRequest.pathVariable("boardId").toLong()
        return ok(articleService.count(boardId))
    }
}