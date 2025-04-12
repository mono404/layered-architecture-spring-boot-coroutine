package com.mono.backend.persistence.comment

import com.mono.backend.comment.Comment
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CoroutineCrudRepository<Comment, Long> {
    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT comment_id FROM comment
                WHERE article_id = :articleId AND parent_comment_id = :parentCommentId
                LIMIT :limit
            ) t
        """
    )
    suspend fun countBy(
        @Param("articleId") articleId: Long,
        @Param("parentCommentId") parentCommentId: Long,
        @Param("limit") limit: Long,
    ): Long

    @Query(
        value = """
            SELECT * FROM (
                SELECT comment_id as t_comment_id FROM comment 
                WHERE article_id = :articleId 
                ORDER BY parent_comment_id asc, comment_id desc 
                LIMIT :limit OFFSET :offset
            ) t left join comment on t.t_comment_id = comment.comment_id
        """
    )
    suspend fun findAll(
        articleId: Long,
        offset: Long,
        limit: Long,
    ): List<Comment>

    @Query(
        value = """
            SELECT count(*) FROM (
                SELECT comment_id FROM comment
                WHERE article_id = :articleId
                LIMIT :limit
            ) t
        """
    )
    suspend fun count(
        articleId: Long,
        limit: Long
    ): Long

    @Query(
        value = """
            SELECT * FROM comment
            WHERE article_id = :articleId
            ORDER BY parent_comment_id asc, comment_id asc
            LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long,
    ): List<Comment>

    @Query(
        value = """
            SELECT * FROM comment
            WHERE article_id = :articleId AND (
                parent_comment_id > :lastParentCommentId OR
                (parent_comment_id = :lastParentCommentId AND comment_id > :lastCommentId) 
            )
            ORDER BY parent_comment_id asc, comment_id asc
            LIMIT :limit
        """
    )
    suspend fun findAllInfiniteScroll(
        articleId: Long,
        lastCommentId: Long,
        lastParentCommentId: Long,
        limit: Long,
    ): List<Comment>
}