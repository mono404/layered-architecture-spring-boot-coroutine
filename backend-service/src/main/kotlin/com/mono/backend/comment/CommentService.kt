package com.mono.backend.comment

import com.mono.backend.common.PageLimitCalculator
import com.mono.backend.comment.request.CommentCreateRequest
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.comment.response.CommentPageResponse
import com.mono.backend.comment.response.CommentResponse
import com.mono.backend.persistence.comment.CommentRepository
import com.mono.backend.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository
) {
    private final val snowflake = Snowflake()

    @Transactional
    suspend fun create(request: CommentCreateRequest): CommentResponse {
        val parent = findParent(request)
        val comment = commentRepository.save(
            Comment(
                commentId = snowflake.nextId(),
                content = request.content,
                parentCommentId = parent?.parentCommentId,
                articleId = request.articleId,
                writerId = request.writerId
            )
        )
        return CommentResponse.from(comment)
    }

    private suspend fun findParent(request: CommentCreateRequest): Comment? {
        val parentCommentId = request.parentCommentId ?: return null
        return commentRepository
            .findById(parentCommentId)
            ?.takeIf { !it.deleted && it.isRoot() }
            ?: throw RuntimeException("Parent comment not found or is deleted")
    }

    suspend fun read(commentId: Long): CommentResponse? {
        return commentRepository.findById(commentId)?.let { CommentResponse.from(it) }
    }

    @Transactional
    suspend fun update(commentId: Long, request: CommentUpdateRequest): CommentResponse {
        val comment = commentRepository.findById(commentId) ?: throw RuntimeException("Comment not found")
        val updatedComment = comment.copy(content = request.content)
        commentRepository.save(updatedComment)
        return CommentResponse.from(updatedComment)
    }

    @Transactional
    suspend fun delete(commentId: Long) {
        commentRepository.findById(commentId)
            ?.takeUnless { it.deleted }
            ?.let { comment ->
                if (hasChildren(comment)) {
                    comment.delete()
                    commentRepository.save(comment) // does not need in JPA
                } else {
                    delete(comment)
                }
            }
    }

    private suspend fun hasChildren(comment: Comment): Boolean {
        return commentRepository.countBy(comment.articleId, comment.commentId, 2L) == 2L
    }

    private suspend fun delete(comment: Comment) {
        commentRepository.delete(comment)
        if (!comment.isRoot()) {
            commentRepository.findById(comment.parentCommentId)
                ?.takeIf { it.deleted && !hasChildren(it) }
                ?.let { delete(it) }
        }
    }

    suspend fun readAll(articleId: Long, page: Long, pageSize: Long): CommentPageResponse {
        println(
            "articleId: $articleId, page: $page, pageSize: $pageSize, limit: ${
                PageLimitCalculator.calculatePageLimit(
                    page,
                    pageSize,
                    10L
                )
            }"
        )
        return CommentPageResponse(
            commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize).stream().map(CommentResponse::from)
                .toList(),
            commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        )
    }

    suspend fun readAllInfiniteScroll(
        articleId: Long,
        lastParentCommentId: Long?,
        lastCommentId: Long?,
        limit: Long
    ): List<CommentResponse> {
        val comments = if (lastParentCommentId == null || lastCommentId == null)
            commentRepository.findAllInfiniteScroll(articleId, limit)
        else
            commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit)

        return comments.map(CommentResponse::from).toList()
    }
}