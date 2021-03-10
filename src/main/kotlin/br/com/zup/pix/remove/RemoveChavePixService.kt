package br.com.zup.pix.remove

import br.com.zup.client.bcb.BcbClient
import br.com.zup.client.bcb.DeletePixKeyRequest
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveInexistenteException
import br.com.zup.shared.validation.UUIDValido
import io.micronaut.http.HttpStatus.OK
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Singleton

@Validated
@Singleton
class RemoveChavePixService(
    private val repository: ChavePixRepository,
    private val bcbClient: BcbClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun remover(@UUIDValido idCliente: String, @UUIDValido idPix: String) {
        val idPixUUID = UUID.fromString(idPix)
        val idClienteUUID = UUID.fromString(idCliente)

        val chavePix = repository.findByIdAndIdCliente(idPixUUID, idClienteUUID)
            .orElseThrow { throw ChaveInexistenteException("Chave de id $idPix não encontrada para o cliente $idCliente") }

        val request = DeletePixKeyRequest(chavePix.chave)
        val response = bcbClient.remover(chavePix.chave, request).also {
            logger.info("Tentativa realizada de remover chave pix [$idPix] do BACEN com status final de ${it.status.name} com o request: $request")
        }

        if (response.status != OK)
            throw IllegalStateException("Erro ao remover a chave no BACEN")

        repository.delete(chavePix)
    }
}