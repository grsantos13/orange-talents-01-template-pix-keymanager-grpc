package br.com.zup.shared.exception.handlers

import br.com.zup.shared.exception.ExceptionHandler
import br.com.zup.shared.exception.ExceptionHandler.StatusWrapper
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class IllegalStateExceptionHandler : ExceptionHandler<IllegalStateException> {
    override fun handle(e: IllegalStateException): StatusWrapper {
        return StatusWrapper(
            Status.FAILED_PRECONDITION
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is IllegalStateException
    }
}