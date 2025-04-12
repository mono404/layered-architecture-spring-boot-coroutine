package com.mono.backend.api

import com.mono.backend.comment.request.CommentCreateRequestV2
import com.mono.backend.comment.response.CommentPageResponseV2
import com.mono.backend.comment.response.CommentResponseV2
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient

class CommentApiV2Test {
    private val restClient = RestClient.create("http://localhost:3001")

    @Test
    fun create() {
        val response1 = create(CommentCreateRequestV2(1L, "my comment1", null, 1L))
        val response2 = create(CommentCreateRequestV2(1L, "my comment2", response1?.path, 1L))
        val response3 = create(CommentCreateRequestV2(1L, "my comment3", response2?.path, 1L))

        println("response1.getPath() = ${response1?.path}")
        println("response1.getCommentId() = ${response1?.commentId}")
        println("\tresponse2.getPath() = ${response2?.path}")
        println("\tresponse2.getCommentId() = ${response2?.commentId}")
        println("\t\tresponse3.getPath() = ${response3?.path}")
        println("\t\tresponse3.getCommentId() = ${response3?.commentId}")

//        response1.getPath() = 0oMGj
//        response1.getCommentId() = 168541524252659712
//            response2.getPath() = 0oMGj00000
//            response2.getCommentId() = 168541578342404096
//                response3.getPath() = 0oMGj0000000000
//                response3.getCommentId() = 168541633568804864
    }

    private fun create(request: CommentCreateRequestV2): CommentResponseV2? {
        return restClient.post()
            .uri("/v2/comments")
            .body(request)
            .retrieve()
            .body(CommentResponseV2::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get()
            .uri("/v2/comments/{commentId}", 168541524252659712)
            .retrieve()
            .body(CommentResponseV2::class.java)
        println("response = $response")
    }

    @Test
    fun delete() {
        restClient.delete()
            .uri("/v2/comments/{commentId}", 168541633568804864)
            .retrieve()
    }

    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
            .retrieve()
            .body(CommentPageResponseV2::class.java)

        println("response.getCommentCount() = ${response?.commentCount}")
        for (comment in response!!.comments) {
            println("comment.getCommentId() = ${comment.commentId}")
        }

//        response.getCommentCount() = 101
//        comment.getCommentId() = 165766378154045442
//        comment.getCommentId() = 165766378187599872
//        comment.getCommentId() = 165766378187599885
//        comment.getCommentId() = 165766378187599892
//        comment.getCommentId() = 165766378187599900
//        comment.getCommentId() = 165766378187599907
//        comment.getCommentId() = 165766378187599917
//        comment.getCommentId() = 165766378187599926
//        comment.getCommentId() = 165766378191794177
//        comment.getCommentId() = 165766378191794183
    }

    @Test
    fun readAllInfiniteScroll() {
        val responses1 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponseV2>>() {})

        println("firstPage")
        for (response in responses1!!) {
            println("response.getCommentId() = ${response.commentId}")
        }

        val lastPath = responses1.last().path
        val responses2 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=$lastPath")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponseV2>>() {})

        println("secondPage")
        for (response in responses2!!) {
            println("response.getCommentId() = ${response.commentId}")
        }
    }

    @Test
    fun countTest() {
        val commentResponseV2 = create(CommentCreateRequestV2(2L, "my comment1", null, 1L))

        val count1 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long::class.java)
        println("count1 = $count1") // 1

        restClient.delete()
            .uri("/v2/comments/{commentId}", commentResponseV2?.commentId)
            .retrieve()

        val count2 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long::class.java)
        println("count2 = $count2") // 0
    }
}