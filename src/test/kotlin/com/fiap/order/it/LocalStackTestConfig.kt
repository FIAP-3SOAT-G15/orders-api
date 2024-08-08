package com.fiap.order.it

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import java.net.URI

@TestConfiguration
@Profile("test")
class LocalStackTestConfig {

    @Value("\${aws.endpoint}")
    private lateinit var endpoint: String

    @Value("\${aws.region}")
    private lateinit var awsRegion: String

    @Value("\${aws.accessKey}")
    private lateinit var accessKey: String

    @Value("\${aws.secretKey}")
    private lateinit var secretKey: String

    @Bean
    fun snsClient(): SnsClient =
        SnsClient.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(awsRegion))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .build()
}