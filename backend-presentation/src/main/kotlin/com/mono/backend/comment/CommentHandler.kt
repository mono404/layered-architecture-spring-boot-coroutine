package com.mono.backend.comment

import com.mono.backend.comment.request.CommentCreateRequest
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.common.DefaultHandler
import com.mono.backend.log.logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import kotlin.jvm.optionals.getOrNull

@Component
class CommentHandler(
    private val commentService: CommentService
): DefaultHandler {
    val log = logger()

    suspend fun read(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return commentService.read(commentId)?.let { ok(it) } ?: noContent()
    }

    suspend fun readAll(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.queryParam("articleId").get().toLong()
        val page = serverRequest.queryParam("page").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        return ok(commentService.readAll(
            articleId = articleId,
            page = page,
            pageSize = pageSize
        ))
    }

    suspend fun readAllInfiniteScroll(serverRequest: ServerRequest): ServerResponse {
        val articleId = serverRequest.queryParam("articleId").get().toLong()
        val pageSize = serverRequest.queryParam("pageSize").get().toLong()
        val lastParentCommentId = serverRequest.queryParam("lastParentCommentId").getOrNull()?.toLong()
        val lastCommentId = serverRequest.queryParam("lastCommentId").getOrNull()?.toLong()
        return ok(commentService.readAllInfiniteScroll(
            articleId = articleId,
            lastParentCommentId = lastParentCommentId,
            lastCommentId = lastCommentId,
            limit = pageSize
        ))
    }

    suspend fun create(serverRequest: ServerRequest): ServerResponse {
        val requestBody = serverRequest.awaitBody(CommentCreateRequest::class)
        return ok(commentService.create(requestBody))
    }

    suspend fun update(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        val requestBody = serverRequest.awaitBody(CommentUpdateRequest::class)
        return ok(commentService.update(commentId, requestBody))
    }

    suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return ok(commentService.delete(commentId))
    }

    suspend fun count(serverRequest: ServerRequest): ServerResponse {
        val commentId = serverRequest.pathVariable("commentId").toLong()
        return ok(commentService.delete(commentId))
    }
}