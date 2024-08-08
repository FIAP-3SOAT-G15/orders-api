package com.fiap.order

import com.fiap.order.it.LocalStackContainerInitializer
import com.fiap.order.it.PostgreSQLContainerInitializer
import com.fiap.order.it.LocalStackTestConfig
import org.junit.jupiter.api.Tag
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@Tag("IntegrationTest")
@ActiveProfiles("test")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class IntegrationTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    initializers = [
        PostgreSQLContainerInitializer::class,
        LocalStackContainerInitializer::class
    ],
    classes = [
        LocalStackTestConfig::class,
        FeignAutoConfiguration::class,
        ServletWebServerFactoryAutoConfiguration::class,
    ]
)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class WithAllDependencies