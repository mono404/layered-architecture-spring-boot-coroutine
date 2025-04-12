package com.mono.backend.like

import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class LikeHandler(
    private val articleLikeService: ArticleLikeService
) : DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return articleLikeService.read(articleId, userId)?.let { ok(it) } ?: noContent()
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        return ok(articleLikeService.count(articleId))
    }

    suspend fun likePessimisticLock1(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return ok(articleLikeService.likePessimisticLock1(articleId, userId))
    }

    suspend fun unlikePessimisticLock1(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return ok(articleLikeService.unlikePessimisticLock1(articleId, userId))
    }

    suspend fun likePessimisticLock2(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return ok(articleLikeService.likePessimisticLock2(articleId, userId))
    }

    suspend fun unlikePessimisticLock2(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return ok(articleLikeService.unlikePessimisticLock2(articleId, userId))
    }

    suspend fun likeOptimisticLock(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return ok(articleLikeService.likeOptimisticLock(articleId, userId))
    }

    suspend fun unlikeOptimisticLock(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.pathVariable("articleId").toLong()
        val userId = serverRequest.pathVariable("userId").toLong()
        return ok(articleLikeService.unlikeOptimisticLock(articleId, userId))
    }
}