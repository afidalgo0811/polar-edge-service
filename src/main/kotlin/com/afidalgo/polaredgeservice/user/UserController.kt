package com.afidalgo.polaredgeservice.user

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import shared.library.order.User

@RestController
class UserController {

  @GetMapping("user")
  fun getUser(@AuthenticationPrincipal oidcUser: OidcUser): Mono<User> =
      Mono.just(
          User(
              oidcUser.preferredUsername,
              oidcUser.givenName,
              oidcUser.familyName,
              mutableListOf("Employee", "customer")))
}
