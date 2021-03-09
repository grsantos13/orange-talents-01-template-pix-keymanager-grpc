package br.com.zup.registra

import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class ChavePix(
    @field:NotNull
    val idCliente: UUID,
    @field:NotBlank
    @field:Size(max = 77)
    val chave: String,
    @field:NotNull
    val tipo: TipoDeChave,
    @field:NotNull
    val tipoDeConta: TipoDeConta
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null

}
