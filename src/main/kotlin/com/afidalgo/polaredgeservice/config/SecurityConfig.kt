package com.afidalgo.polaredgeservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@Configuration
class SecurityConfig {

  @Bean
  fun springSecurityFilterChain(
      http: ServerHttpSecurity,
      clientRegistrationRepository: ReactiveClientRegistrationRepository,
  ): SecurityWebFilterChain {
    return http
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
        .oauth2Login {}
        .logout { it.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)) }
        .csrf { it.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()) }
        .build()
  }

  private fun oidcLogoutSuccessHandler(
      clientRegistrationRepository: ReactiveClientRegistrationRepository,
  ): ServerLogoutSuccessHandler {
    val oidcLogoutSuccess =
        OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)
    oidcLogoutSuccess.setPostLogoutRedirectUri("{baseUrl}")
    return oidcLogoutSuccess
  }

  @Bean
  fun csrfWebFilter(): WebFilter {
    return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
      exchange.response.beforeCommit {
        Mono.defer {
          val csrfToken: Mono<CsrfToken>? =
              exchange.getAttribute<Mono<CsrfToken>>(CsrfToken::class.java.getName())
          exchange.mutate().request { it.header("XSRF-TOKEN", csrfToken.toString()) }.build()
          csrfToken?.then() ?: Mono.empty()
        }
      }
      chain.filter(exchange)
    }
  }

  // Defines a repository to store Access Tokens in the web session
  @Bean
  fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository {
    return WebSessionServerOAuth2AuthorizedClientRepository()
  }
}
