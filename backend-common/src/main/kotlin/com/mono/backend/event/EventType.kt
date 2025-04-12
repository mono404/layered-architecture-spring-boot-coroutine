package com.mono.backend.event

import com.mono.backend.event.payload.*
import org.slf4j.LoggerFactory

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String
) {
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topic.KUKE_BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.KUKE_BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topic.KUKE_BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topic.KUKE_BOARD_VIEW);

    companion object {
        private val logger = LoggerFactory.getLogger(EventType::class.java)

        fun from(type: String): EventType? {
            return try {
                valueOf(type)
            } catch (e: IllegalArgumentException) {
                logger.error("[EventType.from] type={}", type, e)
                null
            }
        }

        object Topic {
            const val KUKE_BOARD_ARTICLE = "kuke-board-article"
            const val KUKE_BOARD_COMMENT = "kuke-board-comment"
            const val KUKE_BOARD_LIKE = "kuke-board-like"
            const val KUKE_BOARD_VIEW = "kuke-board-view"
        }
    }
}