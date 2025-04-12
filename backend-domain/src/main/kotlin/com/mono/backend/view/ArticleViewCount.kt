package com.mono.backend.view

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_view_count")
data class ArticleViewCount(
    @Id
    val articleId: Long,
    val viewCount: Long,
) : Persistable<Long> {
    override fun getId(): Long = articleId
    override fun isNew(): Boolean = true
}
