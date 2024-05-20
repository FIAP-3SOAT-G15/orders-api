package com.fiap.order.driver.web

import com.fiap.order.domain.entities.Customer
import com.fiap.order.driver.web.request.CustomerRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "cliente", description = "Clientes")
@RequestMapping("/customers")
interface CustomerAPI {
    @Operation(summary = "Retorna todos os clientes")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        ],
    )
    @GetMapping
    fun findAll(): ResponseEntity<List<Customer>>

    @Operation(summary = "Retorna cliente pelo documento")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        ],
    )
    @GetMapping("/{customerId}")
    fun getById(
        @Parameter(description = "Identificador do cliente") @PathVariable("customerId") customerId: String,
    ): ResponseEntity<Customer?>

    @Operation(summary = "Pesquisa clientes por nome")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
        ],
    )
    @GetMapping("/search")
    fun searchByName(
        @Parameter(description = "Nome do cliente") @RequestParam("name") name: String,
    ): ResponseEntity<List<Customer>>

    @Operation(summary = "Cadastra cliente")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "422", description = "Cadastro inválido"),
        ],
    )
    @PostMapping()
    fun create(
        @Parameter(description = "Cadastro de cliente") @RequestBody customerRequest: CustomerRequest,
    ): ResponseEntity<Customer>

    @Operation(summary = "Atualiza cadastro de cliente")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        ],
    )
    @PutMapping("/{customerId}")
    fun update(
        @Parameter(description = "Identificador do cliente") @PathVariable("customerId") customerId: String,
        @Parameter(description = "Cadastro de cliente") @RequestBody customerRequest: CustomerRequest,
    ): ResponseEntity<Customer>

    @Operation(summary = "Remove cadastro de cliente")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Operação bem-sucedida"),
            ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            ApiResponse(responseCode = "402", description = "Pagamento necessário"),
        ],
    )
    @DeleteMapping("/{customerId}")
    fun remove(
        @Parameter(description = "Identificador do cliente") @PathVariable("customerId") customerId: String,
    ): ResponseEntity<Customer>
}
