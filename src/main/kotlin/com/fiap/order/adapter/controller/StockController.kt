package com.fiap.order.adapter.controller

import com.fiap.order.domain.entities.Stock
import com.fiap.order.driver.web.StockAPI
import com.fiap.order.driver.web.request.QuantityRequest
import com.fiap.order.usecases.AdjustStockUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class StockController(
    private val adjustStockUseCase: AdjustStockUseCase,
) : StockAPI {
    override fun increment(
        componentNumber: Long,
        quantityRequest: QuantityRequest,
    ): ResponseEntity<Stock> {
        return ResponseEntity.ok(adjustStockUseCase.increment(componentNumber, quantityRequest.quantity))
    }

    override fun decrement(
        componentNumber: Long,
        quantityRequest: QuantityRequest,
    ): ResponseEntity<Stock> {
        return ResponseEntity.ok(adjustStockUseCase.decrement(componentNumber, quantityRequest.quantity))
    }
}
