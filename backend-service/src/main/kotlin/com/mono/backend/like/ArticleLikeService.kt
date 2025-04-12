package com.mono.backend.like

import com.mono.backend.like.response.ArticleLikeResponse
import com.mono.backend.persistence.like.ArticleLikeCountRepository
import com.mono.backend.persistence.like.ArticleLikeRepository
import com.mono.backend.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleLikeService(
    private val articleLikeRepository: ArticleLikeRepository,
    private val articleLikeCountRepository: ArticleLikeCountRepository,
//    private val outboxEventPublisher: OutboxEventPublisher
) {
    private final val snowflake = Snowflake()

    suspend fun read(articleId: Long, userId: Long): ArticleLikeResponse? {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)?.let {
            ArticleLikeResponse.from(it)
        }
    }

    /**
     * update 구문
     */
    @Transactional
    suspend fun likePessimisticLock1(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )
        val result = articleLikeCountRepository.increase(articleId)
        if (result == 0) {
            /**
             * 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화 한다.
             * 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 해둘 수도 있다.
             */
            articleLikeCountRepository.save(
                ArticleLikeCount(articleId, 1L)
            )
        }

//        outboxEventPublisher.publish(
//            type = EventType.ARTICLE_LIKED,
//            payload = ArticleLikedEventPayload(
//                articleLikeId = articleLike.articleLikeId,
//                articleId = articleLike.articleId,
//                userId = articleLike.userId,
//                createdAt = articleLike.createdAt,
//                articleLikeCount = count(articleLike.articleId)
//            ),
//            shardKey = articleLike.articleId
//        )
    }

    @Transactional
    suspend fun unlikePessimisticLock1(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let { articleLike ->
                articleLikeRepository.delete(articleLike)
                articleLikeCountRepository.decrease(articleId)

//                outboxEventPublisher.publish(
//                    type = EventType.ARTICLE_UNLIKED,
//                    payload = ArticleUnlikedEventPayload(
//                        articleLikeId = articleLike.articleLikeId,
//                        articleId = articleLike.articleId,
//                        userId = articleLike.userId,
//                        createdAt = articleLike.createdAt,
//                        articleLikeCount = count(articleLike.articleId)
//                    ),
//                    shardKey = articleLike.articleId
//                )
            }
    }

    /**
     * select ... for update + update
     */
    @Transactional
    suspend fun likePessimisticLock2(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )

        val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
            ?: ArticleLikeCount(articleId, 0L)
        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount) // find가 안된 경우 새로 생성하기 때문에, save 명시적 호출
    }

    @Transactional
    suspend fun unlikePessimisticLock2(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let { articleLike ->
                articleLikeRepository.delete(articleLike)
                val articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                articleLikeCount?.decrease()
            }
    }

    @Transactional
    suspend fun likeOptimisticLock(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike(
                articleLikeId = snowflake.nextId(),
                articleId = articleId,
                userId = userId
            )
        )
        val articleLikeCount = articleLikeCountRepository.findById(articleId)
            ?: ArticleLikeCount(articleId, 0L)
        articleLikeCount.increase()
        articleLikeCountRepository.save(articleLikeCount)
    }

    @Transactional
    suspend fun unlikeOptimisticLock(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let { articleLike ->
                articleLikeRepository.delete(articleLike)
                val articleLikeCount = articleLikeCountRepository.findById(articleId)
                    ?: throw RuntimeException("count not found")
                articleLikeCount.decrease()
            }
    }

    suspend fun count(articleId: Long): Long {
        return articleLikeCountRepository.findById(articleId)?.likeCount ?: 0
    }
}