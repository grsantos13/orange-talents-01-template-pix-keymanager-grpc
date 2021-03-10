package br.com.zup.pix.consulta

import br.com.zup.client.bcb.BcbClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveInexistenteException
import br.com.zup.shared.validation.UUIDValido
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.constraints.NotBlank

@Validated
@Introspected
class FiltroPorIdPixECliente(
    @field:UUIDValido @field:NotBlank val idPix: String,
    @field:UUIDValido @field:NotBlank val idCliente: String,
) : Filtro  {

    override fun filtrar(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixResponse {
        val idPixUUID = UUID.fromString(idPix)
        val idClienteUUID = UUID.fromString(idCliente)

        return repository.findByIdAndIdCliente(idPixUUID, idClienteUUID)
            .map { ChavePixResponse.from(it) }
            .orElseThrow { throw ChaveInexistenteException("Não existe chave de propriedade do cliente $idCliente com id $idPix")}

    }
}