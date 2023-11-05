package com.afidalgo.polaredgeservice.config

import java.security.Principal
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RateLimiterConfig {

  @Bean
  fun KeyResolver(): KeyResolver = KeyResolver {
    it.getPrincipal<Principal>().map(Principal::getName).defaultIfEmpty("anonymous")
  }
}