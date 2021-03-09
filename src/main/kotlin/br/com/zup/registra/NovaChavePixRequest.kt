package br.com.zup.registra

import br.com.gn.shared.validation.Unique
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class NovaChavePixRequest(
    @field:Unique(field = "chave", domainClass = ChavePix::class)
    @field:Size(max = 77)
    val chave: String?,
    @field:NotBlank
    val idCliente: String?,
    @field:NotNull
    val tipo: TipoDeChave?,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
)
