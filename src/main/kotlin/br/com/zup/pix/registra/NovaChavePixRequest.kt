package br.com.zup.pix.registra

import br.com.zup.pix.ChavePix
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.TipoDeChave.ALEATORIA
import br.com.zup.pix.TipoDeConta
import br.com.zup.shared.validation.ChavePixValida
import br.com.zup.shared.validation.UUIDValido
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ChavePixValida
@Introspected
data class NovaChavePixRequest(
    @field:Size(max = 77)
    val chave: String?,
    @UUIDValido
    @field:NotBlank
    val idCliente: String?,
    @field:NotNull
    val tipo: TipoDeChave?,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {
    fun toModel(conta: Conta): ChavePix {
        return ChavePix(
            idCliente = UUID.fromString(idCliente),
            chave = when (tipo) {
                ALEATORIA -> UUID.randomUUID().toString()
                else -> chave!!
            },
            tipo = tipo!!,
            tipoDeConta = tipoDeConta!!,
            conta = conta
        )
    }
}
