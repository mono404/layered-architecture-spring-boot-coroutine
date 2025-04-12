package com.mono.backend.view

import com.mono.backend.persistence.view.ArticleViewCountRepository
import com.mono.backend.persistence.view.ArticleViewDistributedLockRepository
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ArticleViewService(
    private val articleViewCountRepository: ArticleViewCountRepository,
    private val articleViewCountBackUpProcessor: ArticleViewCountBackUpProcessor,
    private val articleViewDistributedLockRepository: ArticleViewDistributedLockRepository
) {
    companion object {
        const val BACK_UP_BATCH_SIZE = 100
        val TTL: Duration = Duration.ofMinutes(10)
    }

    suspend fun increase(articleId: Long, userId: Long): Long? {
        if (articleViewDistributedLockRepository.lock(articleId, userId, TTL) != true) {
            return articleViewCountRepository.read(articleId)
        }

        val count = articleViewCountRepository.increase(articleId)
        if (count != null) {
            if (count % BACK_UP_BATCH_SIZE == 0L) {
                articleViewCountBackUpProcessor.backup(articleId, count)
            }
        }
        return count
    }

    suspend fun count(articleId: Long): Long {
        return articleViewCountRepository.read(articleId)
    }
}