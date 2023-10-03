package com.afidalgo.polaredgeservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PolarEdgeServiceApplicationTests {

  companion object {
    private const val REDIS_PORT = 6379
    @Container
    @JvmStatic
    val redis: GenericContainer<*> =
        GenericContainer(DockerImageName.parse("redis:7.0")).withExposedPorts(REDIS_PORT)

    @DynamicPropertySource
    fun redisProperties(registry: DynamicPropertyRegistry) {
      registry.add("spring.redis.host") { redis.host }
      registry.add("spring.redis.port") { redis.getMappedPort(REDIS_PORT) }
    }
  }

  @Test fun verifyThatSpringContextLoads() {}
}
