package br.com.zup.client.bcb

import br.com.zup.instituicoes.Instituicoes
import br.com.zup.pix.Conta
import br.com.zup.pix.Titular
import br.com.zup.pix.consulta.ChavePixResponse
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    fun toModel(): ChavePixResponse {
        return ChavePixResponse(
            tipo = keyType.tipoDeChave!!,
            chave = key,
            conta = Conta(
                instituicao = Instituicoes.INSTITUICOES[bankAccount.participant]!!,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber,
                titular = Titular(nome = owner.name, cpf = owner.taxIdNumber),
                tipoDeConta = bankAccount.accountType.tipoDeConta
            ),
            registradaEm = createdAt
        )
    }
}