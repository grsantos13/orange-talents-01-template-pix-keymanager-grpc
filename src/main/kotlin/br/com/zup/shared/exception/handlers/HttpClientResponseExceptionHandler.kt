package br.com.zup.shared.exception.handlers

import br.com.zup.shared.exception.ExceptionHandler
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton

@Singleton
class HttpClientResponseExceptionHandler : ExceptionHandler<HttpClientResponseException> {

    override fun handle(e: HttpClientResponseException): ExceptionHandler.StatusWrapper {
        return ExceptionHandler.StatusWrapper(
            Status.INTERNAL.withDescription("Ocorreu um erro ao processar a requisição")
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is HttpClientResponseException
    }
}