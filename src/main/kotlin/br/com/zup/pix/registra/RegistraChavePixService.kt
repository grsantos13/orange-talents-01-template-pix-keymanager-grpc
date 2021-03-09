package br.com.zup.pix.registra

import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.CreatePixKeyRequest
import br.com.zup.client.itau.ItauClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveDuplicadaException
import io.micronaut.http.HttpStatus.CREATED
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class RegistraChavePixService(
    private val repository: ChavePixRepository,
    private val itauClient: ItauClient,
    private val bcbClient: BcbClient
) {

    fun registrar(@Valid chavePixRequest: NovaChavePixRequest): ChavePix {

        if (repository.existsByChave(chavePixRequest.chave!!))
            throw ChaveDuplicadaException("A chave ${chavePixRequest.chave} já existe.")

        val conta = itauClient.buscaContaPorClienteETipoDeConta(
            chavePixRequest.idCliente!!,
            chavePixRequest.tipoDeConta!!.name
        ).body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no sistema do Itaú")

        val pix = chavePixRequest.toModel(conta)
        val pixKeyRequest = CreatePixKeyRequest.from(pix)

        val response = bcbClient.registrar(pixKeyRequest)
        if (response.status != CREATED)
            throw IllegalStateException("Erro ao registrar a chave ${pix.chave} no BACEN")

        pix.atualizar(response.body()!!.key)
        repository.save(pix)

        return pix
    }
}