package com.mono.backend.event.payload

import com.mono.backend.event.EventPayload
import java.time.LocalDateTime

data class ArticleLikedEventPayload(
    val articleLikeId: Long = 0,
    val articleId: Long = 0,
    val userId: Long = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val articleLikeCount: Long = 0
): EventPayload {

}
