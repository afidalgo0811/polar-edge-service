package com.afidalgo.polaredgeservice.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Configuration
class WebEndpoints {

  @Bean
  fun routerFunction(): RouterFunction<ServerResponse> =
      RouterFunctions.route()
          .GET("/catalog-fallback") { ServerResponse.ok().body(Mono.just(""), String::class.java) }
          .POST("/catalog-fallback") {
            ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build()
          }
          .build()
}
