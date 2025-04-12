package com.mono.backend.comment

import com.mono.backend.common.PageLimitCalculator
import com.mono.backend.comment.request.CommentCreateRequestV2
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.comment.response.CommentPageResponseV2
import com.mono.backend.comment.response.CommentResponseV2
import com.mono.backend.persistence.comment.ArticleCommentCountRepository
import com.mono.backend.persistence.comment.CommentRepositoryV2
import com.mono.backend.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentServiceV2(
    private val commentRepositoryV2: CommentRepositoryV2,
    private val articleCommentCountRepository: ArticleCommentCountRepository,
//    private val outboxEventPublisher: OutboxEventPublisher,
) {
    private final val snowflake: Snowflake = Snowflake()

    @Transactional
    suspend fun create(request: CommentCreateRequestV2): CommentResponseV2 {
        val parent = findParent(request)
        val parentCommentPath = parent?.commentPath ?: CommentPath("").path
        val descendantsTopPath = commentRepositoryV2.findDescendantsTopPath(request.articleId, parentCommentPath)
        val comment = commentRepositoryV2.save(
            CommentV2(
                snowflake.nextId(),
                request.content,
                request.articleId,
                request.writerId,
                CommentPath(parentCommentPath).createChildCommentPath(descendantsTopPath).path
            )
        )

        val result = articleCommentCountRepository.increase(request.articleId)
        if (result == 0) {
            articleCommentCountRepository.save(ArticleCommentCount(request.articleId, 1L))
        }

//        outboxEventPublisher.publish(
//            type = EventType.COMMENT_CREATED,
//            payload = CommentCreatedEventPayload(
//                commentId = comment.commentId,
//                content = comment.content,
//                articleId = comment.articleId,
//                writerId = comment.writerId,
//                deleted = comment.deleted,
//                createdAt = comment.createdAt,
//                updatedAt = comment.updatedAt,
//                articleCommentCount = count(comment.articleId)
//            ),
//            shardKey = comment.articleId
//        )

        return CommentResponseV2.from(comment)
    }

    private suspend fun findParent(request: CommentCreateRequestV2): CommentV2? {
        val parentPath = request.parentPath
        if (parentPath.isNullOrEmpty()) {
            return null
        }

        return commentRepositoryV2.findByPath(parentPath)
            ?.takeUnless { it.deleted }
//            .filter(not(CommentV2::deleted))
//            .orElseThrow()
    }

    suspend fun read(commentId: Long): CommentResponseV2? {
        return commentRepositoryV2.findById(commentId)?.let { CommentResponseV2.from(it) }
    }

    @Transactional
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponseV2 {
        val comment = commentRepositoryV2.findById(commentId) ?: throw RuntimeException("Comment not found")
        val updatedComment = comment.copy(content = request.content)
        commentRepositoryV2.save(updatedComment)
        return CommentResponseV2.from(updatedComment)
    }

    @Transactional
    suspend fun delete(commentId: Long) {
        commentRepositoryV2.findById(commentId)
            ?.takeIf { !it.deleted }
            ?.let { comment ->
                if (hasChildren(comment)) {
                    comment.delete()
                    commentRepositoryV2.save(comment)
                } else {
                    delete(comment)
                }

//                outboxEventPublisher.publish(
//                    type = EventType.COMMENT_DELETED,
//                    payload = CommentDeletedEventPayload(
//                        commentId = comment.commentId,
//                        content = comment.content,
//                        articleId = comment.articleId,
//                        writerId = comment.writerId,
//                        deleted = comment.deleted,
//                        createdAt = comment.createdAt,
//                        updatedAt = comment.updatedAt,
//                        articleCommentCount = count(comment.articleId)
//                    ),
//                    shardKey = comment.articleId
//                )
            }
    }

    private suspend fun hasChildren(comment: CommentV2): Boolean {
        return commentRepositoryV2.findDescendantsTopPath(comment.articleId, comment.commentPath) != null
    }

    private suspend fun delete(comment: CommentV2) {
        commentRepositoryV2.delete(comment)
        articleCommentCountRepository.decrease(comment.articleId)
        if (!comment.isRoot()) {
            commentRepositoryV2.findByPath(CommentPath(comment.commentPath).getParentPath())
                ?.takeIf { it.deleted && !hasChildren(it) }
                ?.let { delete(it) }
        }
    }

    suspend fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponseV2 {
        return CommentPageResponseV2(
            commentRepositoryV2.findAll(articleId, (page - 1) * pageSize, pageSize).stream()
                .map { CommentResponseV2.from(it) }.toList(),
            commentRepositoryV2.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        )
    }

    suspend fun readAllInfiniteScroll(articleId: Long, lastPath: String?, pageSize: Long): List<CommentResponseV2> {
        val comments = if (lastPath == null) {
            commentRepositoryV2.findAllInfiniteScroll(articleId, pageSize)
        } else {
            commentRepositoryV2.findAllInfiniteScroll(articleId, lastPath, pageSize)
        }

        return comments.stream()
            .map { CommentResponseV2.from(it) }
            .toList()
    }

    suspend fun count(articleId: Long): Long {
        return articleCommentCountRepository.findById(articleId)?.commentCount ?: 0L
    }
}