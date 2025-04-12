package com.mono.backend.article

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "board_article_count")
data class BoardArticleCount(
    @Id
    val boardId: Long,
    val articleCount: Long
)
