package br.com.zup.pix.consulta

import br.com.zup.client.bcb.BcbClient
import br.com.zup.pix.ChavePixRepository
import br.com.zup.shared.exception.ChaveInexistenteException
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus.OK
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Validated
@Introspected
class FiltroPorChave(
    @field:NotBlank @Size(max = 77) val chave: String
) : Filtro {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun filtrar(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixResponse {
        return repository.findByChave(chave)
            .map {
                ChavePixResponse(
                    IdPix = it.id,
                    IdCliente = it.idCliente,
                    tipo = it.tipo,
                    chave = it.chave,
                    conta = it.conta,
                    registradaEm = it.criadaEm
                )
            }
            .orElseGet {
                logger.info("Chave $chave não encontrada internamente, inicial busca no BACEN.")
                val response = bcbClient.consultar(chave)
                when (response.status) {
                    OK -> response.body()!!.toModel()
                    else -> throw ChaveInexistenteException("Chave não encontrada com o valor $chave")
                }
            }
    }
}