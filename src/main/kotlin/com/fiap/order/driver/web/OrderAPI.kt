package com.fiap.order.driver.web

import com.fiap.order.domain.entities.Order
import com.fiap.order.driver.web.request.OrderRequest
import com.fiap.order.driver.web.response.PendingOrderResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "pedido", description = "Pedidos")
@RequestMapping("/orders")
interface OrderAPI {
    
    @Operation(summary = "Retorna pedido pelo número")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        ],
    )
    @GetMapping("/{orderNumber}")
    fun getByOrderNumber(
        @Parameter(description = "Número do pedido") @PathVariable orderNumber: Long,
    ): ResponseEntity<Order>

    @Operation(summary = "Retorna todos os pedidos")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        ],
    )
    @GetMapping
    fun findAll(): ResponseEntity<List<Order>>

    @Operation(summary = "Retorna todos os pedidos ativos")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        ],
    )
    @GetMapping("/status/active")
    fun findAllActive(): ResponseEntity<List<Order>>
    
    @Operation(summary = "Retorna pedidos por status")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "400", description = "Status inválido"),
        ],
    )
    @GetMapping("/status/{status}")
    fun findByStatus(
        @Parameter(description = "Status do pedido") @PathVariable status: String,
    ): ResponseEntity<List<Order>>

    @Operation(summary = "Retorna pedidos de cliente por status")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "400", description = "Status inválido"),
        ],
    )
    @GetMapping("/status/{status}/customer")
    fun findByStatusAndCustomerId(
        @Parameter(description = "Status do pedido") @PathVariable status: String,
        @Parameter(description = "Apelido do cliente") @RequestParam(required = false) customerId: String,
    ): ResponseEntity<List<Order>>

    @Operation(summary = "Retorna pedidos de cliente")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        ],
    )
    @GetMapping("customer")
    fun findByCustomerId(
        @Parameter(description = "Apelido do cliente") @RequestParam(required = false) customerId: String,
    ): ResponseEntity<List<Order>>

    @Operation(summary = "Cria pedido")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            ApiResponse(responseCode = "404", description = "Cliente não encontrado (quando informado documento)"),
            ApiResponse(responseCode = "422", description = "Pedido sem itens"),
        ],
    )
    @PostMapping
    fun create(
        @Parameter(description = "Cadastro de pedido") @RequestBody orderRequest: OrderRequest,
    ): ResponseEntity<PendingOrderResponse>

    @Operation(summary = "Confirma pedido")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            ApiResponse(responseCode = "400", description = "Pedido não pode ser confirmado"),
        ],
    )
    @PostMapping("/{orderNumber}/confirm")
    fun confirm(@PathVariable orderNumber: Long) : ResponseEntity<Order>
    
    @Operation(summary = "Atualiza status de pedido em preparo")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            ApiResponse(responseCode = "400", description = "Pedido não pode ser iniciado"),
        ],
    )
    @PostMapping("/{orderNumber}/start")
    fun start(
        @Parameter(description = "Número do pedido") @PathVariable orderNumber: Long,
    ): ResponseEntity<Order>

    @Operation(summary = "Atualiza status de pedido pronto")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            ApiResponse(responseCode = "400", description = "Pedido não pode ser marcado como preparado"),
        ],
    )
    @PostMapping("/{orderNumber}/finish")
    fun finish(
        @Parameter(description = "Número do pedido") @PathVariable orderNumber: Long,
    ): ResponseEntity<Order>

    @Operation(summary = "Atualiza status de pedido para finalizado")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            ApiResponse(responseCode = "400", description = "Pedido não pode ser finalizado"),
        ],
    )
    @PostMapping("/{orderNumber}/complete")
    fun complete(
        @Parameter(description = "Número do pedido") @PathVariable orderNumber: Long,
    ): ResponseEntity<Order>

    @Operation(summary = "Cancela pedido")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado"),
        ],
    )
    @PostMapping("/{orderNumber}/cancel")
    fun cancel(
        @Parameter(description = "Número do pedido") @PathVariable orderNumber: Long,
    ): ResponseEntity<Order>
}
