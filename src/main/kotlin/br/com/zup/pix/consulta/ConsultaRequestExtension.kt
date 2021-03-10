package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.CHAVE
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.IDPIXEIDCLIENTE

fun ConsultaChavePixRequest.toFilter(): Filtro {
    return when (consultaCase) {
        IDPIXEIDCLIENTE -> FiltroPorIdPixECliente(idPixEIdCliente.idPix, idPixEIdCliente.idCliente)
        CHAVE -> FiltroPorChave(chave)
        else -> throw IllegalArgumentException("Chave inválida ou não informada.")
    }
}