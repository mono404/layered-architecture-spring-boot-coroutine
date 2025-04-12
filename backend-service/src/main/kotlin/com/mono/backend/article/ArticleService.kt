package com.mono.backend.article

import com.mono.backend.article.request.ArticleCreateRequest
import com.mono.backend.article.request.ArticleUpdateRequest
import com.mono.backend.article.response.ArticlePageResponse
import com.mono.backend.article.response.ArticleResponse
import com.mono.backend.common.PageLimitCalculator
import com.mono.backend.persistence.article.ArticleRepository
import com.mono.backend.persistence.article.BoardArticleCountRepository
import com.mono.backend.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val boardArticleCountRepository: BoardArticleCountRepository,
//    private val outboxEventPublisher: OutboxEventPublisher
) {
    private final val snowflake = Snowflake()

    suspend fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article(
                articleId = snowflake.nextId(),
                title = request.title,
                content = request.content,
                boardId = request.boardId,
                writerId = request.writerId
            )
        )
        boardArticleCountRepository.increase(request.boardId).takeIf { it == 0 }?.let {
            boardArticleCountRepository.save(BoardArticleCount(request.boardId, 1L))
        }

//        outboxEventPublisher.publish(
//            type = EventType.ARTICLE_CREATED,
//            payload = ArticleCreatedEventPayload(
//                articleId = article.articleId,
//                title = article.title,
//                content = article.content,
//                boardId = article.boardId,
//                writerId = article.writerId,
//                createdAt = article.createdAt,
//                updatedAt = article.updatedAt,
//                boardArticleCount = count(article.boardId)
//            ),
//            shardKey = article.boardId
//        )

        return ArticleResponse.from(article)
    }

    @Transactional
    suspend fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articleRepository.findById(articleId) ?: throw RuntimeException("Article not found")
        val updatedArticle = article.copy(title = request.title, content = request.content)
        articleRepository.save(updatedArticle) // does not need in JPA

//        outboxEventPublisher.publish(
//            type = EventType.ARTICLE_UPDATED,
//            payload = ArticleUpdatedEventPayload(
//                articleId = article.articleId,
//                title = article.title,
//                content = article.content,
//                boardId = article.boardId,
//                writerId = article.writerId,
//                createdAt = article.createdAt,
//                updatedAt = article.updatedAt,
//            ),
//            shardKey = article.boardId
//        )

        return ArticleResponse.from(updatedArticle)
    }

    suspend fun read(articleId: Long): ArticleResponse? {
        return articleRepository.findById(articleId)?.let { ArticleResponse.from(it) }
    }

    @Transactional
    suspend fun delete(articleId: Long) {
        articleRepository.findById(articleId)?.let { article ->
            articleRepository.delete(article)
            boardArticleCountRepository.decrease(article.boardId)

//            outboxEventPublisher.publish(
//                type = EventType.ARTICLE_DELETED,
//                payload = ArticleDeletedEventPayload(
//                    articleId = article.articleId,
//                    title = article.title,
//                    content = article.content,
//                    boardId = article.boardId,
//                    writerId = article.writerId,
//                    createdAt = article.createdAt,
//                    updatedAt = article.updatedAt,
//                    boardArticleCount = count(article.boardId)
//                ),
//                shardKey = article.boardId
//            )
        }
    }

    suspend fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse {
        return ArticlePageResponse(
            articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).map(ArticleResponse::from).toList(),
            articleRepository.count(boardId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        )
    }

    suspend fun readAllInfiniteScroll(boardId: Long, pageSIze: Long, lastArticleId: Long?): List<ArticleResponse> {
        val articles = if (lastArticleId == null) {
            articleRepository.findAllInfiniteScroll(boardId, pageSIze)
        } else {
            articleRepository.findAllInfiniteScroll(boardId, pageSIze, lastArticleId)
        }
        return articles.map(ArticleResponse::from).toList()
    }

    suspend fun count(boardId: Long): Long {
        return boardArticleCountRepository.findById(boardId)?.let(BoardArticleCount::articleCount) ?: 0L
    }
}