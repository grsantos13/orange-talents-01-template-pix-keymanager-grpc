package br.com.zup.pix.registra

import br.com.zup.client.ItauClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveDuplicadaException
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class RegistraChavePixService(
    private val itauClient: ItauClient,
    private val repository: ChavePixRepository
) {

    fun registrar(@Valid chavePixRequest: NovaChavePixRequest): ChavePix {

        if (repository.existsByChave(chavePixRequest.chave!!))
            throw ChaveDuplicadaException("A chave ${chavePixRequest.chave} já existe.")

        val conta = itauClient.buscaContaPorClienteETipoDeConta(
            chavePixRequest.idCliente!!,
            chavePixRequest.tipoDeConta!!.name
        ).body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no sistema do Itaú")

        val pix = chavePixRequest.toModel(conta)

        repository.save(pix)
        return pix

    }
}