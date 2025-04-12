package com.mono.backend.article.response

import com.mono.backend.article.Article
import java.time.LocalDateTime

data class ArticleResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long, // 게시판 아이디
    val writerId: Long, // 작성자 아이디
    val createdAt: LocalDateTime = LocalDateTime.now(), // 생성일시
    val updatedAt: LocalDateTime = createdAt // 수정일시
) {
    companion object {
        fun from(article: Article): ArticleResponse {
            return ArticleResponse(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt!!,
                updatedAt = article.updatedAt!!
            )
        }
    }
}