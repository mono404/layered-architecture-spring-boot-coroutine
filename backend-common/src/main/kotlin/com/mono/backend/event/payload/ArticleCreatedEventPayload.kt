package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class ArticleCreatedEventPayload(
    val articleId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val boardId: Long? = null,
    val writerId: Long? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val boardArticleCount: Long? = null,
): EventPayload