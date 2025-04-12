package com.mono.backend.comment

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "article_comment_count")
data class ArticleCommentCount(
    @Id
    val articleId: Long,
    val commentCount: Long,
)
