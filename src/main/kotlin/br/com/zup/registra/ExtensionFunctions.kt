package br.com.zup.registra

import br.com.zup.RegistraChavePixRequest
import br.com.zup.TipoDeChave.UNKNOWN_TIPO_DE_CHAVE
import br.com.zup.TipoDeConta.UNKNOWN_TIPO_DE_CONTA

fun RegistraChavePixRequest.toRequestModel(): NovaChavePixRequest {
    return NovaChavePixRequest(
        chave = chave,
        idCliente = idCliente,
        tipo = if (tipoDeChave == UNKNOWN_TIPO_DE_CHAVE) null else TipoDeChave.valueOf(tipoDeChave.name),
        tipoDeConta = if (tipoDeConta == UNKNOWN_TIPO_DE_CONTA) null else TipoDeConta.valueOf(tipoDeConta.name)
    )
}