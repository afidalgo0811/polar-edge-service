package com.afidalgo.polaredgeservice.user

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController {

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }

  @GetMapping("user")
  fun getUser(@AuthenticationPrincipal oidcUser: OidcUser): Mono<User> {
    logger.info("Fetching information about the currently authenticated user")
    return Mono.just(
        User(
            oidcUser.preferredUsername,
            oidcUser.givenName,
            oidcUser.familyName,
            mutableListOf("employee", "customer")))
  }
}