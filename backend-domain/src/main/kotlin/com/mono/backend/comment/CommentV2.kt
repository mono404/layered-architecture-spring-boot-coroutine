package com.mono.backend.comment

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "comment_v2")
data class CommentV2(
    @Id
    val commentId: Long,
    val content: String,
    val articleId: Long,
    val writerId: Long,
    @Column("path")
    val commentPath: String,
    var deleted: Boolean = false,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
) : Persistable<Long> {
    fun isRoot(): Boolean = CommentPath(commentPath).isRoot()


    fun delete() {
        deleted = true
    }

    override fun getId(): Long = commentId
    override fun isNew(): Boolean = createdAt == null
}