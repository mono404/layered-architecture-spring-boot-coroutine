package com.mono.backend.view

import com.mono.backend.persistence.view.ArticleViewCountBackUpRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountBackUpProcessor(
    private val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository,
//    private val outboxEventPublisher: OutboxEventPublisher
) {
    @Transactional
    suspend fun backup(articleId: Long, viewCount: Long) {
        val result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount)
        if (result == 0) {
            val articleViewCount = articleViewCountBackUpRepository.findById(articleId)
            if(articleViewCount == null) {
                articleViewCountBackUpRepository.save(ArticleViewCount(articleId, viewCount))

//                    outboxEventPublisher.publish(
//                        type = EventType.ARTICLE_VIEWED,
//                        payload = ArticleViewedEventPayload(
//                            articleId = articleId,
//                            articleViewCount = viewCount
//                        ),
//                        shardKey = articleId
//                    )
            }
        }
    }
}