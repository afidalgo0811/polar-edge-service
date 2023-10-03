import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
  id("org.springframework.boot") version "3.1.4"
  id("io.spring.dependency-management") version "1.1.3"
  kotlin("jvm") version "1.8.22"
  kotlin("plugin.spring") version "1.8.22"
  id("com.diffplug.spotless") version "6.19.0"
}

group = "com.afidalgo"

version = "0.0.1-SNAPSHOT"

java { sourceCompatibility = JavaVersion.VERSION_17 }

repositories { mavenCentral() }

extra["springCloudVersion"] = "2022.0.4"

extra["testcontainersVersion"] = "1.18.0"

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.cloud:spring-cloud-starter-gateway")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  implementation(
      "org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
  implementation("org.springframework.session:spring-session-data-redis")
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  implementation("org.springframework.retry:spring-retry")
  testImplementation("org.testcontainers:junit-jupiter")
}

dependencyManagement {
  imports {
    mavenBom(
        "org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}

tasks.withType<Test> { useJUnitPlatform() }

configure<SpotlessExtension> {
  kotlin {
    // by default the target is every '.kt' and '.kts` file in the java sourcesets
    ktfmt() // has its own section below
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}

tasks.named<BootBuildImage>("bootBuildImage") {
  imageName.set(project.name)
  environment.set(environment.get() + mapOf("BP_JVM_VERSION" to "17"))
  docker {
    publishRegistry {
      username.set(project.findProperty("registryUsername").toString())
      password.set(project.findProperty("registryToken").toString())
      url.set(project.findProperty("registryUrl").toString())
    }
  }
}
