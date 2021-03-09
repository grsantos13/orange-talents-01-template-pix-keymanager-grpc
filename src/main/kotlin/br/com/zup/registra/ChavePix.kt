package br.com.zup.registra

import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ChavePix(
    @field:NotNull
    val idCliente: UUID,
    @field:NotBlank
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
