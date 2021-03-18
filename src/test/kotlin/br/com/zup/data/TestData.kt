package br.com.zup.data

import br.com.zup.ListaChavesPixResponse
import br.com.zup.client.bcb.AccountType
import br.com.zup.client.bcb.BankAccount
import br.com.zup.client.bcb.KeyType
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
                bankAccount = BankAccount(
                    participant = "60746948",
                    branch = "0001",
                    accountNumber = "000001",
                    accountType = AccountType.CACC
                ),
                owner = Owner(
                    type = OwnerType.NATURAL_PERSON,
                    name = "Usuário anônimo",
                    taxIdNumber = "13579808642"
                ),
                createdAt = LocalDateTime.now()
            )
        }

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
    }


}