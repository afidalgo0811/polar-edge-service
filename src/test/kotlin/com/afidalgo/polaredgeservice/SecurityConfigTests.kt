package com.afidalgo.polaredgeservice

import com.afidalgo.polaredgeservice.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest
@Import(SecurityConfig::class)
class SecurityConfigTests {

  @Autowired lateinit var webClient: WebTestClient
  @MockBean lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

  private fun testClientRegistration(): ClientRegistration {
    return ClientRegistration.withRegistrationId("test")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .clientId("test")
        .authorizationUri("https://sso.polarbookshop.com/auth")
        .tokenUri("https://sso.polarbookshop.com/token")
        .redirectUri("https://polarbookshop.com")
        .build()
  }

  @Test
  fun `when logout authenticate and CsrfToken then 302`() {
    `when`(clientRegistrationRepository.findByRegistrationId("test"))
        .thenReturn(Mono.just(testClientRegistration()))

    webClient
        .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
        .mutateWith(SecurityMockServerConfigurers.csrf())
        .post()
        .uri("/logout")
        .exchange()
        .expectStatus()
        .isFound
  }
}
