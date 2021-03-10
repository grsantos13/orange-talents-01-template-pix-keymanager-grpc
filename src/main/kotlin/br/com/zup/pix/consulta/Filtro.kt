package br.com.zup.pix.consulta

import br.com.zup.client.bcb.BcbClient
import br.com.zup.pix.ChavePix
import br.com.zup.pix.ChavePixRepository

interface Filtro {
    fun filtrar(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixResponse
}