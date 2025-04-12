package com.mono.backend.like

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "article_like")
data class ArticleLike(
    @Id
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    @CreatedDate
    val createdAt: LocalDateTime? = null
) : Persistable<Long> {
    override fun getId(): Long = articleLikeId

    override fun isNew(): Boolean = createdAt == null
}