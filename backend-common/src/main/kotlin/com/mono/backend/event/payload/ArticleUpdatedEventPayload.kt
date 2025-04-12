package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class ArticleUpdatedEventPayload(
    val articleId: Long = 0,
    val title: String = "",
    val content: String = "",
    val boardId: Long = 0,
    val writerId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
): EventPayload {

}
