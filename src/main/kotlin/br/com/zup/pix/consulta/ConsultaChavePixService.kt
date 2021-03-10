package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.CHAVE
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.IDPIXEIDCLIENTE
import br.com.zup.client.bcb.BcbClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository
import javax.inject.Singleton

@Singleton
class ConsultaChavePixService(
    private val repository: ChavePixRepository,
    private val bcbClient: BcbClient
) {

    fun consultar(request: ConsultaChavePixRequest): ChavePix {
        if (request.consultaCase == CHAVE){
            repository.findByChave(request.chave)
        }else if(request.consultaCase == IDPIXEIDCLIENTE){
            repository.findByIdAndIdCliente()
        }
    }
}