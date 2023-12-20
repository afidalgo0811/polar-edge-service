package com.afidalgo.polaredgeservice

import com.afidalgo.polaredgeservice.config.SecurityConfig
import com.afidalgo.polaredgeservice.user.User
import com.afidalgo.polaredgeservice.user.UserController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@WebFluxTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTests {

  @Autowired lateinit var webClient: WebTestClient
  @MockBean lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository
  val objectMapper: ObjectMapper = ObjectMapper()

  @Test
  fun whenNotAuthenticatedThen401() {
    webClient.get().uri("/user").exchange().expectStatus().isUnauthorized
  }

  @Test
  fun whenAuthenticatedThenReturnUser() {
    val expectedUser = User("jon.snow", "Jon", "Snow", mutableListOf("employee", "customer"))
    webClient
        .mutateWith(configureMockOidcLogin(expectedUser))
        .get()
        .uri("/user")
        .exchange()
        .expectStatus()
        .is2xxSuccessful
        .expectBody(User::class.java)
        .value { assert(it.equals(expectedUser)) }
  }

  private fun configureMockOidcLogin(
      expectedUser: User
  ): SecurityMockServerConfigurers.OidcLoginMutator {
    return SecurityMockServerConfigurers.mockOidcLogin().idToken {
      it.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.userName)
          .claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName)
          .claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName)
    }
  }
}
