package com.fiap.order.adapter.client.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class StockApiInterceptor: RequestInterceptor {
    
    override fun apply(template: RequestTemplate?) {
        val requestAttr = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes)
        val apiToken = requestAttr.request.getHeader("authorization")
        template?.header("authorization", apiToken)
    }
}
