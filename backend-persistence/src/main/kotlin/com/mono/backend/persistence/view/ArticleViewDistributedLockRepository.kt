package com.mono.backend.persistence.view

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration


@Repository
class ArticleViewDistributedLockRepository(
    private val redisTemplate: ReactiveStringRedisTemplate
) {
    companion object {

        // view::article::{articleId}::user::{userId}::lock
        const val KET_FORMAT = "view::article::{articleId}::user::{userId}::lock"
    }

    suspend fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean? {
        val key = generateKey(articleId, userId)
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl).awaitSingleOrNull()
    }

    private fun generateKey(articleId: Long, userId: Long): String {
        return String.format(KET_FORMAT, articleId, userId)
    }
}