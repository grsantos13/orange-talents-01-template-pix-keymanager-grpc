package br.com.zup.shared.exception.handlers

import br.com.zup.shared.exception.ChaveInexistenteException
import br.com.zup.shared.exception.ExceptionHandler
import br.com.zup.shared.exception.ExceptionHandler.StatusWrapper
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveInexistenteHandler : ExceptionHandler<ChaveInexistenteException> {
    override fun handle(e: ChaveInexistenteException): StatusWrapper {
        return StatusWrapper(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveInexistenteException
    }
}