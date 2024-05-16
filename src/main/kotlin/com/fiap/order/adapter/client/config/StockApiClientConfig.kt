package com.fiap.order.adapter.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StockApiClientConfig {

    @Bean
    fun productApiInterceptor(): StockApiInterceptor{
        return StockApiInterceptor()
    }
}