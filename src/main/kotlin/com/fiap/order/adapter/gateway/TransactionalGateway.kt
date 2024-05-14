package com.fiap.order.adapter.gateway

interface TransactionalGateway {
    fun <T> transaction(code: () -> T): T
}
