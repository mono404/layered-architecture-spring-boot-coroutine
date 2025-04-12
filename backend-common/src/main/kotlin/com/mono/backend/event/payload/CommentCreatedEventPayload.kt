package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class CommentCreatedEventPayload(
    val commentId: Long = 0,
    val content: String = "",
    val path: String = "",
    val articleId: Long = 0,
    val writerId: Long = 0,
    val deleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val articleCommentCount: Long = 0
): EventPayload {

}
