package com.mono.backend.comment

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "comment")
data class Comment(
    @Id
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long,
    val writerId: Long,
    var deleted: Boolean = false,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    constructor(commentId: Long, content: String, parentCommentId: Long?, articleId: Long, writerId: Long) : this(
        commentId = commentId,
        content = content,
        parentCommentId = parentCommentId ?: commentId,
        articleId = articleId,
        writerId = writerId,
    )

    fun isRoot() = parentCommentId == commentId
    fun delete() {
        deleted = true
    }

    override fun getId(): Long = commentId
    override fun isNew(): Boolean = createdAt == null
}
