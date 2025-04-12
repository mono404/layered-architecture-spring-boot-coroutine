package com.mono.backend.persistence.like

import com.mono.backend.like.ArticleLike
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleLikeRepository : CoroutineCrudRepository<ArticleLike, Long> {
    suspend fun findByArticleIdAndUserId(articleId: Long, userId: Long): ArticleLike?
}