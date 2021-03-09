package br.com.zup.pix.remove

import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveInexistenteException
import br.com.zup.shared.validation.UUIDValido
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton

@Validated
@Singleton
class RemoveChavePixService(private val repository: ChavePixRepository) {

    fun remover(@UUIDValido idCliente: String, @UUIDValido idPix: String) {
        val idPixUUID = UUID.fromString(idPix)
        val idClienteUUID = UUID.fromString(idCliente)

        val chavePix = repository.findByIdAndIdCliente(idPixUUID, idClienteUUID)
            .orElseThrow { throw ChaveInexistenteException("Chave de id $idPix não encontrada para o cliente $idCliente") }

        repository.delete(chavePix)

    }
}