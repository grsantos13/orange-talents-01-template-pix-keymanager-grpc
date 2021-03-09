package br.com.zup.client.itau

import br.com.zup.client.ContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.erp.url}")
interface ItauClient {
    @Get("/api/v1/clientes/{idCliente}/contas")
    fun buscaContaPorClienteETipoDeConta(
        @PathVariable idCliente: String,
        @QueryValue tipo: String
    ): HttpResponse<ContaResponse>
}