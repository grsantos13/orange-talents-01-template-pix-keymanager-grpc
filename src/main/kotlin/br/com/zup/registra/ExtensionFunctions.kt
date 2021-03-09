package br.com.zup.registra

import br.com.zup.RegistraChavePixRequest

fun RegistraChavePixRequest.toRequestModel(): NovaChavePixRequest {
    return NovaChavePixRequest(
        chave = chave,
        idCliente = idCliente,
        tipo = TipoDeChave.valueOf(tipoDeChave.name),
        tipoDeConta = TipoDeConta.valueOf(tipoDeConta.name)
    )
}