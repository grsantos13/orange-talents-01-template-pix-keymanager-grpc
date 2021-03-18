package br.com.zup.data

import br.com.zup.ListaChavesPixResponse
import br.com.zup.client.bcb.AccountType
import br.com.zup.client.bcb.BankAccount
import br.com.zup.client.bcb.CreatePixKeyRequest
import br.com.zup.client.bcb.CreatePixKeyResponse
import br.com.zup.client.bcb.KeyType
import br.com.zup.client.bcb.KeyType.PHONE
import br.com.zup.client.bcb.Owner
import br.com.zup.client.bcb.OwnerType
import br.com.zup.client.bcb.PixKeyDetailsResponse
import br.com.zup.pix.ChavePix
import br.com.zup.pix.Conta
import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.TipoDeConta
import br.com.zup.pix.Titular
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TestData {
    companion object {
        fun criarPixKeyDetailsResponse(): PixKeyDetailsResponse {
            return PixKeyDetailsResponse(
                keyType = KeyType.EMAIL,
                key = "email@email.com",
                bankAccount = bankAccountElse(),
                owner = owner(),
                createdAt = LocalDateTime.now()
            )
        }

        private fun owner() = Owner(
            type = OwnerType.NATURAL_PERSON,
            name = "Gustavo Santos",
            taxIdNumber = "20783911076"
        )

        private fun bankAccountItau() = BankAccount(
            participant = "60701190",
            branch = "0001",
            accountNumber = "000001",
            accountType = AccountType.CACC
        )

        private fun bankAccountElse() = BankAccount(
            participant = "60746948",
            branch = "0001",
            accountNumber = "000001",
            accountType = AccountType.CACC
        )

        fun criarChave(idCliente: UUID, chave: String?, tipo: TipoDeChave): ChavePix {
            return ChavePix(
                idCliente = idCliente,
                chave = chave ?: UUID.randomUUID().toString(),
                tipo = tipo,
                conta = Conta(
                    instituicao = "ITAÚ UNIBANCO S.A.",
                    titular = Titular(nome = "Gustavo Santos", cpf = "12332112233"),
                    agencia = "1234",
                    numeroDaConta = "987657",
                    tipoDeConta = TipoDeConta.CONTA_CORRENTE
                )
            )
        }

        fun mapToChavePixGrpcResponse(chaves: List<ChavePix>): List<ListaChavesPixResponse.ChavePix> {
            return chaves.map {
                val registradaEm = it.registradaEm.atZone(ZoneId.of("UTC")).toInstant()
                ListaChavesPixResponse.ChavePix.newBuilder()
                    .setChave(it.chave)
                    .setIdPix(it.id.toString())
                    .setTipoDaChave(br.com.zup.TipoDeChave.valueOf(it.tipo.name))
                    .setTipoDaConta(br.com.zup.TipoDeConta.valueOf(it.conta.tipoDeConta.name))
                    .setRegistradaEm(
                        Timestamp.newBuilder()
                            .setSeconds(registradaEm.epochSecond)
                            .setNanos(registradaEm.nano)
                            .build()
                    )
                    .build()
            }
        }

        fun criarCreatePixKeyRequest(): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                key = "+5519999999999",
                keyType = PHONE,
                bankAccount = bankAccountItau(),
                owner = owner()
            )
        }

        fun criarCreatePixKeyResponse(): CreatePixKeyResponse {
            return CreatePixKeyResponse(
                key = "+5519999999999",
                keyType = KeyType.PHONE,
                bankAccount = bankAccountItau(),
                owner = owner(),
                createdAt = LocalDateTime.now()
            )
        }

    }


}