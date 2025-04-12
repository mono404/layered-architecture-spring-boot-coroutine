package com.mono.backend.article.request

data class ArticleCreateRequest(
    val title: String,
    val content: String,
    val writerId: Long,
    val boardId: Long
)