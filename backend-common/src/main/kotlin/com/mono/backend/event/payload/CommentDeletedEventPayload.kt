package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class CommentDeletedEventPayload(
    val commentId: Long? = null,
    val content: String? = null,
    val path: String? = null,
    val articleId: Long? = null,
    val writerId: Long? = null,
    val deleted: Boolean? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val articleCommentCount: Long? = null
): EventPayload {

}
