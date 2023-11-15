package com.afidalgo.polaredgeservice.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler

@EnableWebFluxSecurity
class SecurityConfig {

  @Bean
  fun springSecurityFilterChain(
      http: ServerHttpSecurity,
      clientRegistrationRepository: ReactiveClientRegistrationRepository
  ): SecurityWebFilterChain =
      http
          .authorizeExchange {
            it.pathMatchers("/", "/*.css", "/*.js", "/favicon.ico")
                .permitAll()
                .pathMatchers(HttpMethod.GET, "/books/**")
                .permitAll()
                .anyExchange()
                .authenticated()
          }
          .exceptionHandling {
            it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
          }
          .oauth2Login(Customizer.withDefaults())
          .logout {
            it.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
          }
          .build()

  private fun oidcLogoutSuccessHandler(
      clientRegistrationRepository: ReactiveClientRegistrationRepository
  ): ServerLogoutSuccessHandler {
    val oidcLogoutSuccess =
        OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)
    oidcLogoutSuccess.setPostLogoutRedirectUri("{baseUrl}")
    return oidcLogoutSuccess
  }
}
