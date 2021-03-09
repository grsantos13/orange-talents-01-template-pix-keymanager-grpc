package br.com.zup.client.bcb

import br.com.zup.pix.ChavePix
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.TipoDeConta
import java.time.LocalDateTime

data class CreatePixKeyRequest(
    val key: String,
    val keyType: KeyType,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest
) {
    companion object {
        fun from(chavePix: ChavePix): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                key = chavePix.chave,
                keyType = KeyType.from(chavePix.tipo),
                bankAccount = BankAccountRequest(
                    Conta.ISPB,
                    chavePix.conta.agencia,
                    chavePix.conta.numeroDaConta,
                    AccountType.from(chavePix.conta.tipoDeConta)
                ),
                owner = OwnerRequest(
                    type = OwnerType.NATURAL_PERSON,
                    name = chavePix.conta.titular.nome,
                    taxIdNumber = chavePix.conta.titular.cpf
                )
            )
        }
    }
}

data class OwnerRequest(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
)

enum class OwnerType {
    NATURAL_PERSON, LEGAL_PERSON
}

data class BankAccountRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
)

enum class AccountType(private val tipoDeConta: TipoDeConta) {
    CACC(TipoDeConta.CONTA_CORRENTE),
    SVGS(TipoDeConta.CONTA_POUPANCA);

    companion object {
        fun from(tipo: TipoDeConta): AccountType {
            val accountTypeMap = AccountType.values().associateBy(AccountType::tipoDeConta)
            return accountTypeMap[tipo] ?: throw IllegalArgumentException("AccountType inexistente para $tipo")
        }
    }

}

enum class KeyType(private val tipoDeChave: TipoDeChave?) {
    CPF(TipoDeChave.CPF),
    CNPJ(null),
    PHONE(TipoDeChave.CELULAR),
    EMAIL(TipoDeChave.EMAIL),
    RANDOM(TipoDeChave.ALEATORIA);

    companion object {
        fun from(tipo: TipoDeChave): KeyType {
            val keyTypeMap = KeyType.values().associateBy(KeyType::tipoDeChave)
            return keyTypeMap[tipo] ?: throw IllegalArgumentException("KeyType inexistente para $tipo")
        }
    }

}

data class CreatePixKeyResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest,
    val createdAt: LocalDateTime
)