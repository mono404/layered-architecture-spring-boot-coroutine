package com.mono.backend

import com.mono.backend.article.ArticleRouter
import com.mono.backend.comment.CommentRouter
import com.mono.backend.comment.CommentRouterV2
import com.mono.backend.exrate.ExRateRouter
import com.mono.backend.like.LikeRouter
import com.mono.backend.view.ViewRouter
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Component
class BackendRouter(
    private val exRateRouter: ExRateRouter,
    private val articleRouter: ArticleRouter,
    private val commentRouter: CommentRouter,
    private val commentRouterV2: CommentRouterV2,
    private val likeRouter: LikeRouter,
    private val viewRouter: ViewRouter
) {
    @Bean
    fun backendRoutes(): RouterFunction<ServerResponse> = coRouter {
        "/v1".nest {
            "/ex-rate".nest(exRateRouter.exRateRoutes())
            "/articles".nest(articleRouter.articleRoutes())
            "/comments".nest(commentRouter.commentRoutes())
            "/article-like".nest {
                "/articles/{articleId}".nest(likeRouter.likeRoutes())
            }
            "/article-views".nest {
                "/articles/{articleId}".nest(viewRouter.viewRoutes())
            }
        }
        "/v2".nest {
            "/comments".nest(commentRouterV2.commentRoutesV2())
        }
    }
}