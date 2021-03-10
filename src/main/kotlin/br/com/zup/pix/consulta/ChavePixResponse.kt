package br.com.zup.pix.consulta

import br.com.zup.pix.ChavePix
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave
import java.time.LocalDateTime
import java.util.*

data class ChavePixResponse(
    val IdPix: UUID? = null,
    val IdCliente: UUID? = null,
    val tipo: TipoDeChave,
    val chave: String,
    val conta: Conta,
    val registradaEm: LocalDateTime
) {

    companion object {
        fun from(chave: ChavePix): ChavePixResponse {
            return ChavePixResponse(
                IdPix = chave.id,
                IdCliente = chave.idCliente,
                tipo = chave.tipo,
                chave = chave.chave,
                conta = chave.conta,
                registradaEm = chave.criadaEm
            )
        }
    }
}