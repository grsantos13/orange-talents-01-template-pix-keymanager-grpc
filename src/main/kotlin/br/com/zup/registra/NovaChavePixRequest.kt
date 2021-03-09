package br.com.zup.registra

import br.com.gn.shared.validation.Unique
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class NovaChavePixRequest(
    @field:Unique(field = "chave", domainClass = ChavePix::class)
    val chave: String,
    @field:NotBlank
    val idCliente: String,
    @field:NotNull
    val tipo: TipoDeChave,
    @field:NotNull
    val tipoDeConta: TipoDeConta
)
