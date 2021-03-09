package br.com.zup.client

import br.com.zup.pix.Conta
import br.com.zup.pix.Titular
import java.util.*

data class ContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
){
    fun toModel() : Conta {
        val titularConta = Titular(
            nome = titular.nome,
            cpf = titular.cpf
        )
        return Conta(
            instituicao = instituicao.nome,
            agencia = agencia,
            numeroDaConta = numero,
            titular = titularConta
        )
    }
}

data class TitularResponse(
    val id: UUID,
    val nome: String,
    val cpf: String
)

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)
