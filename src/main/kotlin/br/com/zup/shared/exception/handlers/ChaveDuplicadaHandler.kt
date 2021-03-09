package br.com.zup.shared.exception.handlers

import br.com.zup.shared.exception.ChaveDuplicadaException
import br.com.zup.shared.exception.ExceptionHandler
import br.com.zup.shared.exception.ExceptionHandler.StatusWrapper
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveDuplicadaHandler : ExceptionHandler<ChaveDuplicadaException> {
    override fun handle(e: ChaveDuplicadaException): StatusWrapper {
        return StatusWrapper(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveDuplicadaException
    }
}