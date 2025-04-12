package com.mono.backend.api

import com.mono.backend.comment.request.CommentCreateRequest
import com.mono.backend.comment.request.CommentUpdateRequest
import com.mono.backend.comment.response.CommentPageResponse
import com.mono.backend.comment.response.CommentResponse
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient


class CommentApiTest {
    private val restClient = RestClient.create("http://localhost:3001")

    @Test
    fun create() {
        val response1 = createComment(CommentCreateRequest(1L, "my comment1", null, 1L))
        println("comment1 : ${response1?.commentId}")
        val response2 = createComment(CommentCreateRequest(1L, "my comment1", response1?.commentId, 1L))
        println("comment2 : ${response2?.commentId}")
        val response3 = createComment(CommentCreateRequest(1L, "my comment1", response1?.commentId, 1L))
        println("comment3 : ${response3?.commentId}")


//        comment1 : 168528039945891840
//        comment2 : 168528040210132992
//        comment3 : 168528040252076032
    }

    @Test
    fun readTest() {
        val response = read(168528039945891840)
        println("response : $response")
    }

    @Test
    fun updateTest() {
        update(168528039945891840)
        val response = read(168528039945891840)
        println(response)
    }

    @Test
    fun deleteTest() {
        delete(168528039945891840)
        val response = read(168528039945891840)
        println("response : $response")
    }

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v1/comments?articleId=1&page=1&pageSize=10")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.commentCount = ${response?.commentCount}")
        for (comment in response?.comments!!) {
            if (comment.commentId != comment.parentCommentId)
                print("\t")
            println("comment.commentId = ${comment.commentId}")
        }
    }

    @Test
    fun readAllInfiniteScroll() {
        val response1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("firstPage")
        for (comment in response1!!) {
            if (comment.commentId != comment.parentCommentId)
                print("\t")
            println("comment.commentId = ${comment.commentId}")
        }

        val lastCommentId = response1.last().commentId
        val lastParentCommentId = response1.last().parentCommentId

        val response2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastCommentId=$lastCommentId&lastParentCommentId=$lastParentCommentId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("secondPage")
        for (comment in response2!!) {
            if (comment.commentId != comment.parentCommentId)
                print("\t")
            println("comment.commentId = ${comment.commentId}")
        }
    }

    private fun createComment(request: CommentCreateRequest): CommentResponse? {
        return restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    private fun read(commentId: Long) = restClient.get()
        .uri("/v1/comments/$commentId")
        .retrieve()
        .body(CommentResponse::class.java)

    private fun update(commentId: Long) {
        restClient.put()
            .uri("/v1/comments/$commentId")
            .body(CommentUpdateRequest("updated comment"))
            .retrieve()
            .body(CommentResponse::class.java)
    }

    private fun delete(commentId: Long) {
        restClient.delete()
            .uri("/v1/comments/$commentId")
            .retrieve()
    }
}