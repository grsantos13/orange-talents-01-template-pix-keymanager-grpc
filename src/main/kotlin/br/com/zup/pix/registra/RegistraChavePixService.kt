package br.com.zup.pix.registra

import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.CreatePixKeyRequest
import br.com.zup.client.itau.ItauClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveDuplicadaException
import io.micronaut.http.HttpStatus.CREATED
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.Valid
import kotlin.math.log

@Validated
@Singleton
class RegistraChavePixService(
    private val repository: ChavePixRepository,
    private val itauClient: ItauClient,
    private val bcbClient: BcbClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun registrar(@Valid chavePixRequest: NovaChavePixRequest): ChavePix {

        if (repository.existsByChave(chavePixRequest.chave!!))
            throw ChaveDuplicadaException("A chave ${chavePixRequest.chave} já existe.")

        val conta = itauClient.buscaContaPorClienteETipoDeConta(
            chavePixRequest.idCliente!!,
            chavePixRequest.tipoDeConta!!.name
        ).body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no sistema do Itaú")

        logger.info("Conta encontrada no sistema do itaú [$conta]")

        val pix = chavePixRequest.toModel(conta)
        logger.info("$pix")
        val pixKeyRequest = CreatePixKeyRequest.from(pix)

        val response = bcbClient.registrar(pixKeyRequest).also {
            logger.info("Tentativa de registro no BACEN realizada com status final de ${it.status.name} \n chave enviada: $pixKeyRequest")
        }

        if (response.status != CREATED)
            throw IllegalStateException("Erro ao registrar a chave ${pix.chave} no BACEN")

        pix.atualizar(response.body()!!.key)
        repository.save(pix)

        return pix
    }
}