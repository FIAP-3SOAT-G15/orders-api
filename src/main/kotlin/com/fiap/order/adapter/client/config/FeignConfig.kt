package com.fiap.order.adapter.client.config

import org.springframework.cloud.openfeign.clientconfig.FeignClientConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {

    @Bean
    fun feignClientConfigurer(): FeignClientConfigurer {
        return object : FeignClientConfigurer {
            override fun inheritParentConfiguration(): Boolean {
                return false
            }
        }
    }

    @Bean
    fun stockApiInterceptor(): StockApiInterceptor{
        return StockApiInterceptor()
    }

}