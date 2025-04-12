package com.mono.backend.like

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_like_count")
data class ArticleLikeCount(
    @Id
    val articleId: Long,
    var likeCount: Long,
    @Version
    val version: Long = 0,
) {
    fun increase() {
        this.likeCount++
    }

    fun decrease() {
        this.likeCount--
    }
}
