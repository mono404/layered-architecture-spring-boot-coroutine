package com.mono.backend.persistence.view

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ArticleViewCountRepository(
    private val redisTemplate: ReactiveStringRedisTemplate,
) {
    companion object {
        // view::article::{articleId}::view_count
        const val KET_FORMAT = "view::article::%s::view_count"
    }

    suspend fun read(articleId: Long): Long = redisTemplate.opsForValue()
        .get(generateKey(articleId))
        .awaitSingleOrNull()?.toLong()
        ?: 0

    suspend fun increase(articleId: Long): Long? {
        return redisTemplate.opsForValue().increment(generateKey(articleId)).awaitSingleOrNull()
    }

    private fun generateKey(articleId: Long): String {
        return String.format(KET_FORMAT, articleId)
    }
}