package br.com.zup.pix.consulta

import br.com.zup.ConsultaChavePixRequest
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.CHAVE
import br.com.zup.ConsultaChavePixRequest.ConsultaCase.IDPIXEIDCLIENTE
import io.micronaut.validation.validator.Validator
import javax.validation.ConstraintViolationException

fun ConsultaChavePixRequest.toFilter(validator: Validator): Filtro {
    val filtro = when (consultaCase) {
        IDPIXEIDCLIENTE -> FiltroPorIdPixECliente(idPixEIdCliente.idPix, idPixEIdCliente.idCliente)
        CHAVE -> FiltroPorChave(chave)
        else -> throw IllegalArgumentException("Chave inválida ou não informada.")
    }

    val validation = validator.validate(filtro)
    if (validation.isNotEmpty())
        throw throw ConstraintViolationException(validation)

    return filtro
}