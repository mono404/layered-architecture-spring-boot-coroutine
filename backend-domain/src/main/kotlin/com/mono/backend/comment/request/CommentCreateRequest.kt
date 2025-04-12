package com.mono.backend.comment.request

data class CommentCreateRequest(
    val articleId: Long,
    val content: String,
    val parentCommentId: Long?,
    val writerId: Long
)
